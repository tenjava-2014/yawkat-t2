package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.TenJava;
import com.tenjava.entries.yawkat.t2.module.config.ModuleConfiguration;
import com.tenjava.entries.yawkat.t2.module.config.ModuleConfigurationProvider;
import com.tenjava.entries.yawkat.t2.module.config.YamlModuleConfigurationProvider;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Represents a part of the behaviour of the plugin.
 *
 * @author yawkat
 */
public abstract class Module implements Listener {
    private static ModuleConfigurationProvider CONFIGURATION_PROVIDER;

    private ModuleConfiguration config;

    /**
     * Load and initialize a module by class.
     */
    public static void load(Class<? extends Module> moduleClass) {
        try {
            Module module = moduleClass.newInstance();
            TenJava.runTaskOnStartup(module::init0);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void init0() {
        synchronized (Module.class) {
            if (CONFIGURATION_PROVIDER == null) {
                CONFIGURATION_PROVIDER = YamlModuleConfigurationProvider
                        .load(new File(TenJava.getInstance().getDataFolder(), "modules.yml"));
            }
        }
        config = CONFIGURATION_PROVIDER.getConfiguration(this);

        getConfig().setDefault("enabled", true);
        if (getConfig().get("enabled")) {
            init();
        }
        getConfig().save();
    }

    protected void init() {
        Bukkit.getPluginManager().registerEvents(this, TenJava.getInstance());
    }

    public ModuleConfiguration getConfig() {
        return config;
    }
}

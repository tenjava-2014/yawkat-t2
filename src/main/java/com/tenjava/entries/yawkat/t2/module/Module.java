package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.TenJava;
import com.tenjava.entries.yawkat.t2.module.config.ModuleConfiguration;
import com.tenjava.entries.yawkat.t2.module.config.ModuleConfigurationProvider;
import com.tenjava.entries.yawkat.t2.module.config.YamlModuleConfigurationProvider;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Represents a part of the behaviour of the plugin.
 *
 * @author yawkat
 */
public abstract class Module implements Listener {
    /**
     * A map of all enabled modules.
     */
    private static final Map<Class<? extends Module>, Module> modules = new HashMap<>();

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

    /**
     * Get an enabled module by class or an empty optional if the module is not enabled or not loaded yet.
     */
    @SuppressWarnings("unchecked")
    public static <M extends Module> Optional<M> getModule(Class<M> type) {
        if (modules.containsKey(type)) {
            return Optional.of((M) modules.get(type));
        } else {
            return Optional.empty();
        }
    }

    private void init0() {
        synchronized (Module.class) {
            // load configuration provider if it isn't loaded yet
            if (CONFIGURATION_PROVIDER == null) {
                CONFIGURATION_PROVIDER = YamlModuleConfigurationProvider
                        .load(new File(TenJava.getInstance().getDataFolder(), "modules.yml"));
            }
        }
        // get config from provider
        config = CONFIGURATION_PROVIDER.getConfiguration(this);

        getConfig().setDefault("enabled", true);
        // if enabled = false in config, don't call #init, don't enable
        if (getConfig().get("enabled")) {
            // only put to module map when enabled
            modules.put(getClass(), this);
            init();
        }
        // save config in case new defaults have been added
        getConfig().save();
    }

    protected void init() {
        Bukkit.getPluginManager().registerEvents(this, TenJava.getInstance());
    }

    public ModuleConfiguration getConfig() {
        return config;
    }
}

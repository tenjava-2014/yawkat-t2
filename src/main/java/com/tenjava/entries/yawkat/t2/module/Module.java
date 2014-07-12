package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.TenJava;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Represents a part of the behaviour of the plugin.
 *
 * @author yawkat
 */
public abstract class Module implements Listener {
    /**
     * Load and initialize a module by class.
     */
    public static void load(Class<? extends Module> moduleClass) {
        try {
            Module module = moduleClass.newInstance();
            TenJava.runTaskOnStartup(module::init);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void init() {
        Bukkit.getPluginManager().registerEvents(this, TenJava.getInstance());
    }
}

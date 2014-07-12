package com.tenjava.entries.yawkat.t2.module.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * ModuleConfiguration implementation that uses the bukkit yaml configuration API.
 *
 * @author yawkat
 */
class YamlModuleConfiguration implements ModuleConfiguration {
    /**
     * Runnable that saves this configuration (specified by creator).
     */
    private final Runnable saver;
    /**
     * Our config section.
     */
    private final ConfigurationSection configuration;

    public YamlModuleConfiguration(Runnable saver, ConfigurationSection configuration) {
        this.saver = saver;
        this.configuration = configuration;
    }

    @Override
    public void save() {
        saver.run();
    }

    @Override
    public void setDefault(String key, Object defaultValue) {
        if (!configuration.isSet(key)) {
            configuration.set(key, defaultValue);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        T v = (T) configuration.get(key);
        // must be set via setDefault
        if (v == null) {
            throw new NullPointerException("No config value for " + key);
        }
        return v;
    }
}

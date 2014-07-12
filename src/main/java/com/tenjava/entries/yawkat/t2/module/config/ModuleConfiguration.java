package com.tenjava.entries.yawkat.t2.module.config;

/**
 * Represents the configuration of a single module.
 *
 * @author yawkat
 */
public interface ModuleConfiguration {
    /**
     * Save this module's configuration.
     */
    void save();

    /**
     * Assign a default value to the given configuration entry.
     */
    void setDefault(String key, Object defaultValue);

    /**
     * Get a configuration entry.
     */
    <T> T get(String key);
}

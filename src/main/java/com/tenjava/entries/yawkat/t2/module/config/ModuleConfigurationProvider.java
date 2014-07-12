package com.tenjava.entries.yawkat.t2.module.config;

import com.tenjava.entries.yawkat.t2.module.Module;

/**
 * Provider for ModuleConfiguration instances by module.
 *
 * @author yawkat
 */
public interface ModuleConfigurationProvider {
    ModuleConfiguration getConfiguration(Module module);
}

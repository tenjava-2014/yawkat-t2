package com.tenjava.entries.yawkat.t2.module.config;

import com.tenjava.entries.yawkat.t2.module.Module;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * ModuleConfigurationProvider that uses the bukkit yaml config api (single file).
 *
 * @author yawkat
 */
public class YamlModuleConfigurationProvider implements ModuleConfigurationProvider {
    private final File file;
    private final FileConfiguration configuration;

    private YamlModuleConfigurationProvider(File file, FileConfiguration configuration) {
        this.file = file;
        this.configuration = configuration;
    }

    /**
     * Create a ModuleConfigurationProvider from a file (will save as well).
     */
    public static ModuleConfigurationProvider load(File file) {
        return new YamlModuleConfigurationProvider(file, YamlConfiguration.loadConfiguration(file));
    }

    @Override
    public ModuleConfiguration getConfiguration(Module module) {
        // use class name as entry; this isn't all that flexible but works well enough for my setup
        String name = module.getClass().getSimpleName();
        ConfigurationSection section = configuration.getConfigurationSection(name);
        if (section == null) {
            // not in the config (yet), add
            section = configuration.createSection(name);
        }
        // #save is used as the saver for the configuration.
        return new YamlModuleConfiguration(this::save, section);
    }

    /**
     * Save this config.
     */
    private void save() {
        try {
            file.getParentFile().mkdirs();
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

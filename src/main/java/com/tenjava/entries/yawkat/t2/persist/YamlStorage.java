package com.tenjava.entries.yawkat.t2.persist;

import com.tenjava.entries.yawkat.t2.TenJava;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * YAML implementation of PersistentStorage.
 *
 * @author yawkat
 */
class YamlStorage implements PersistentStorage {
    /**
     * The unique name of this storage.
     */
    private final String name;

    private FileConfiguration configuration;

    public YamlStorage(String name) {
        this.name = name;
    }

    /**
     * Ensure our data file is in memory, load it otherwise.
     */
    private synchronized void ensureLoaded() {
        if (configuration == null) {
            File storageFile = getStorageFile();
            configuration = YamlConfiguration.loadConfiguration(storageFile);
        }
    }

    /**
     * Get the yaml file we should read from / write to.
     */
    private File getStorageFile() {
        return new File(TenJava.getInstance().getDataFolder(), this.name + ".yml");
    }

    @Override
    public void load(UUID player) {
        // just make sure our data file is in memory
        ensureLoaded();
    }

    @Override
    public void save(UUID player) {
        // save our data file, don't unload because we can't do that without random access :(
        File storageFile = getStorageFile();
        try {
            //noinspection ResultOfMethodCallIgnored
            storageFile.getParentFile().mkdirs();
            configuration.save(storageFile);
        } catch (IOException e) {
            TenJava.getInstance().getLogger().log(Level.SEVERE, e, () -> "Failed to save " + storageFile);
        }
    }

    @Override
    public Optional<Object> get(UUID player, String key) {
        return Optional.ofNullable(getPlayerStore(player).get(key, null));
    }

    @Override
    public void set(UUID player, String key, Object value) {
        getPlayerStore(player).set(key, value);
    }

    /**
     * Get the yaml section of the given player.
     */
    private ConfigurationSection getPlayerStore(UUID player) {
        ConfigurationSection section = configuration.getConfigurationSection(player.toString());
        // create section if missing
        if (section == null) {
            section = configuration.createSection(player.toString());
        }
        return section;
    }
}

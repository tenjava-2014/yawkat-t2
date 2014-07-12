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
 * @author yawkat
 */
class YamlStorage implements PersistentStorage {
    private final String name;

    private FileConfiguration configuration;

    public YamlStorage(String name) {
        this.name = name;
    }

    private synchronized void ensureLoaded() {
        if (configuration == null) {
            File storageFile = getStorageFile();
            configuration = YamlConfiguration.loadConfiguration(storageFile);
        }
    }

    private File getStorageFile() {
        return new File(TenJava.getInstance().getDataFolder(), this.name + ".yml");
    }

    @Override
    public void load(UUID player) {
        ensureLoaded();
    }

    @Override
    public void save(UUID player) {
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

    private ConfigurationSection getPlayerStore(UUID player) {
        return configuration.getConfigurationSection(player.toString());
    }
}

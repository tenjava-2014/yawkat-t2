package com.tenjava.entries.yawkat.t2.persist;

import com.tenjava.entries.yawkat.t2.util.player.DataDescriptor;
import com.tenjava.entries.yawkat.t2.util.player.PlayerData;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.entity.Player;

/**
 * Represents the persisted data for a player.
 *
 * @author yawkat
 */
public class PersistedPlayerData {
    /**
     * DataDescriptor used to cache data for each player.
     */
    private static final DataDescriptor<PersistedPlayerData> PERSISTED_PLAYER_DATA_DATA =
            new DataDescriptor<>((Function<Player, PersistedPlayerData>) pl ->
                    new PersistedPlayerData(pl.getUniqueId()));

    /**
     * PersistentStorage file.
     */
    private static final PersistentStorage STORAGE = new YamlStorage("players");

    private final UUID player;

    /**
     * Initialize the persistent storage.
     */
    public static void init() {
        PersistedPlayerDataListener.register();
    }

    /**
     * Get the persistent data for a player.
     */
    public static PersistedPlayerData get(Player player) {
        return PlayerData.forPlayer(player).get(PERSISTED_PLAYER_DATA_DATA);
    }

    /**
     * Preload the data for a player.
     */
    static void preload(UUID uuid) {
        STORAGE.load(uuid);
    }

    /**
     * Save and unload the data for a player.
     */
    static void unload(UUID uuid) {
        STORAGE.save(uuid);
    }

    private PersistedPlayerData(UUID player) {
        this.player = player;
    }

    /**
     * Get a persisted field for this player. If it doesn't exist, return the default value.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) STORAGE.get(player, key).orElse(defaultValue);
    }

    /**
     * Persist some data for this player.
     */
    public void set(String key, Object value) {
        STORAGE.set(player, key, value);
    }
}

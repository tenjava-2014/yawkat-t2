package com.tenjava.entries.yawkat.t2.persist;

import com.tenjava.entries.yawkat.t2.util.player.DataDescriptor;
import com.tenjava.entries.yawkat.t2.util.player.PlayerData;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.entity.Player;

/**
 * @author yawkat
 */
public class PersistedPlayerData {
    private static final DataDescriptor<PersistedPlayerData> PERSISTED_PLAYER_DATA_DATA =
            new DataDescriptor<>((Function<Player, PersistedPlayerData>) pl ->
                    new PersistedPlayerData(pl.getUniqueId()));

    private static final PersistentStorage STORAGE = new YamlStorage("players");

    private final UUID player;

    public static void init() {
        PersistedPlayerDataListener.register();
    }

    public static PersistedPlayerData get(Player player) {
        return PlayerData.forPlayer(player).get(PERSISTED_PLAYER_DATA_DATA);
    }

    static void preload(UUID uuid) {
        STORAGE.load(uuid);
    }

    static void unload(UUID uuid) {
        STORAGE.save(uuid);
    }

    private PersistedPlayerData(UUID player) {
        this.player = player;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) STORAGE.get(player, key).orElse(defaultValue);
    }

    public void set(String key, Object value) {
        STORAGE.set(player, key, value);
    }
}

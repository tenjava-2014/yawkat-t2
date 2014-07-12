package com.tenjava.entries.yawkat.t2.persist;

import java.util.Optional;
import java.util.UUID;

/**
 * API for a player data storage.
 *
 * @author yawkat
 */
interface PersistentStorage {
    /**
     * Load the data for a player.
     */
    void load(UUID player);

    /**
     * Save and unload the data for a player.
     */
    void save(UUID player);

    /**
     * Get a value from this player's storage or an empty Optional if it doesn't exist (yet).
     */
    Optional<Object> get(UUID player, String key);

    /**
     * Set a value in the storage of the given player.
     */
    void set(UUID player, String key, Object value);
}

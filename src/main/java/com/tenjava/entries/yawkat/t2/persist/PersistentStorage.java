package com.tenjava.entries.yawkat.t2.persist;

import java.util.Optional;
import java.util.UUID;

/**
 * @author yawkat
 */
interface PersistentStorage {
    void load(UUID player);

    void save(UUID player);

    Optional<Object> get(UUID player, String key);

    void set(UUID player, String key, Object value);
}

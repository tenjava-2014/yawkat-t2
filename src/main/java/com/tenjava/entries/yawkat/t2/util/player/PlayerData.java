package com.tenjava.entries.yawkat.t2.util.player;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;

/**
 * API used to store arbitrary metadata for online players. Consists of "cells" that can be read and written to. Cells
 * are described by a DataDescriptor instance.
 *
 * @author yawkat
 */
public class PlayerData {
    static {
        PlayerDataClearer.register();
    }

    /**
     * Weak map of all PlayerData instance for online players. When a player logs out, their entry should be removed.
     */
    private static final Map<Player, PlayerData> DATA_MAP = new WeakHashMap<>();

    /**
     * The cells we store data in. The index of a DataDescriptor in this list is their DataDescriptor#instanceId.
     */
    private final List<Object> cells = new ArrayList<>();

    private PlayerData() {}

    /**
     * Get the PlayerData instance for a given player.
     */
    public static synchronized PlayerData forPlayer(Player player) {
        // check if we already have an entry for that player, otherwise create one.
        return DATA_MAP.computeIfAbsent(player, pl -> new PlayerData());
    }

    /**
     * Reset the entry for a player.
     */
    static synchronized void clearPlayer(Player player) {
        DATA_MAP.remove(player);
    }

    /**
     * Get the value of a given DataDescriptor. If no value is set for this descriptor, use the default one.
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> T get(DataDescriptor<T> descriptor) {
        // descriptor null check in #ensureCapacity
        ensureCapacity(descriptor);

        // get our value
        Object v = cells.get(descriptor.getInstanceId());
        // create default if necessary
        if (v == null) {
            v = descriptor.getDefaultValueSupplier().get();
            if (v == null) {
                throw new NullPointerException("Invalid default value for descriptor: null");
            }
            // put default (if value is mutable we don't want to regenerate it each time and lose changes)
            cells.set(descriptor.getInstanceId(), v);
        }
        return (T) v;
    }

    /**
     * Set the value of a cell defined by the given descriptor.
     *
     * Values must be non-null (use Optional if required).
     */
    public synchronized <T> PlayerData set(DataDescriptor<T> descriptor, T value) {
        // descriptor null check in #ensureCapacity
        Preconditions.checkNotNull(value, "value");

        ensureCapacity(descriptor);

        // fill the cell with the value
        cells.set(descriptor.getInstanceId(), value);
        return this;
    }

    /**
     * Expand our cell list so we can read / store the given descriptor.
     */
    private void ensureCapacity(DataDescriptor<?> descriptor) {
        Preconditions.checkNotNull(descriptor, "descriptor");

        while (cells.size() <= descriptor.getInstanceId()) {
            cells.add(null);
        }
    }
}

package com.tenjava.entries.yawkat.t2.util.player;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.entity.Player;

/**
 * Describes a cell in a PlayerData object. Can be kept as a static field, for example
 * <br/>
 * <code>
 * private static final DataDescriptor<Integer> KILL_COUNT = new DataDescriptor(0);
 * </code>
 *
 * @author yawkat
 */
public class DataDescriptor<T> {
    private static final AtomicInteger instanceCounter = new AtomicInteger();

    /**
     * Cell ID of this descriptor.
     */
    private final int instanceId = instanceCounter.getAndIncrement();

    /**
     * Default value to use on first read in #PlayerData.
     */
    private final Function<Player, T> defaultValueCreator;

    public DataDescriptor(Function<Player, T> defaultValueSupplier) {
        this.defaultValueCreator = defaultValueSupplier;
    }

    public DataDescriptor(Supplier<T> defaultValueSupplier) {
        this(pl -> defaultValueSupplier.get());
    }

    public DataDescriptor(T defaultValue) {
        this(pl -> defaultValue);
    }

    int getInstanceId() {
        return instanceId;
    }

    Function<Player, T> getDefaultValueCreator() {
        return defaultValueCreator;
    }
}

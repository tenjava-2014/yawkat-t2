package com.tenjava.entries.yawkat.t2.util.async;

import com.tenjava.entries.yawkat.t2.TenJava;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Bukkit;

/**
 * TaskPipeline implementation that runs the task synchronously (on the main minecraft thread).
 *
 * @author yawkat
 */
class SyncPipeline<I, O> extends TaskPipeline<I, O> {
    public SyncPipeline(Pipeline<I> previous, Function<I, O> task) {
        super(previous, task);
    }

    @Override
    protected void compute(I input, Consumer<O> recipient) {
        Bukkit.getScheduler().callSyncMethod(TenJava.getInstance(), () -> {
            O result = runTask(input);
            recipient.accept(result);
            return result;
        });
    }
}

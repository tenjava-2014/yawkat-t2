package com.tenjava.entries.yawkat.t2.util.async;

import com.tenjava.entries.yawkat.t2.TenJava;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Bukkit;

/**
 * TaskPipeline implementation that runs the task asynchronously.
 *
 * @author yawkat
 */
class AsyncPipeline<I, O> extends TaskPipeline<I, O> {
    public AsyncPipeline(Pipeline<I> previous, Function<I, O> task) {
        super(previous, task);
    }

    @Override
    protected void compute(I input, Consumer<O> recipient) {
        Bukkit.getScheduler().runTaskAsynchronously(TenJava.getInstance(), () -> {
            O result = runTask(input);
            // forward to next link
            recipient.accept(result);
        });
    }
}

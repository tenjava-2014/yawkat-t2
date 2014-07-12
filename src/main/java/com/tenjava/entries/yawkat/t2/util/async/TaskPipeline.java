package com.tenjava.entries.yawkat.t2.util.async;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Abstract class for SyncPipeline and AsyncPipeline. Describes a pipeline piece that has a previous pipeline and
 * performs a task on that previous pipeline's result.
 *
 * @author yawkat
 */
abstract class TaskPipeline<I, O> extends Pipeline<O> {
    private final Pipeline<I> previous;

    private final Function<I, O> task;

    public TaskPipeline(Pipeline<I> previous, Function<I, O> task) {
        this.previous = previous;
        this.task = task;
    }

    /**
     * Run our task. Called by subclasses from the threads they specify (sync or async respectively).
     */
    protected O runTask(I input) {
        return task.apply(input);
    }

    /**
     * Compute the result of the previous pipeline and give it to the recipient upon completion.
     */
    protected void computePrevious(Consumer<I> recipient) {
        previous.compute(recipient);
    }

    @Override
    void compute(Consumer<O> recipient) {
        computePrevious(input -> compute(input, recipient));
    }

    /**
     * Compute our value. Implementations must not directly call #runTask but rather do so in their threads (sync /
     * async).
     */
    protected abstract void compute(I input, Consumer<O> recipient);
}

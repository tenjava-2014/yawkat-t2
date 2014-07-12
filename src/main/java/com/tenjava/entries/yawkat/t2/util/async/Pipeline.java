package com.tenjava.entries.yawkat.t2.util.async;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Abstract pipeline class. This class and its implementations are immutable: calling the builder methods in this class
 * will return a new pipeline, not the same one.
 *
 * @author yawkat
 */
public abstract class Pipeline<O> {
    /**
     * Compute the value of this pipeline and send it to the given consumer upon completion.
     */
    abstract void compute(Consumer<O> recipient);

    /**
     * Execute a task synchronously with the result of this pipeline as the argument.
     */
    public <N> Pipeline<N> sync(Function<O, N> next) {
        return new SyncPipeline<>(this, next);
    }

    /**
     * Execute a task asynchronously with the result of this pipeline as the argument.
     */
    public <N> Pipeline<N> async(Function<O, N> next) {
        return new AsyncPipeline<>(this, next);
    }

    /**
     * Finish this pipeline (i. e. run it).
     */
    private void finish() {
        compute(o -> {});
    }

    /**
     * Finish this pipeline and give the result to the given consumer which will be executed synchronously.
     */
    public void finishSync(Consumer<O> consumer) {
        sync(o -> {
            consumer.accept(o);
            return null;
        }).finish();
    }

    /**
     * Finish this pipeline and give the result to the given consumer which will be executed asynchronously.
     */
    public void finishAsync(Consumer<O> consumer) {
        async(o -> {
            consumer.accept(o);
            return null;
        }).finish();
    }
}

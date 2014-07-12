package com.tenjava.entries.yawkat.t2.util.async;

import java.util.function.Consumer;

/**
 * First element of a pipeline with a static value (argument to the rest of the pipeline).
 *
 * @author yawkat
 */
class InitPipeline<O> extends Pipeline<O> {
    private final O value;

    InitPipeline(O value) {
        this.value = value;
    }

    @Override
    void compute(Consumer<O> recipient) {
        recipient.accept(value);
    }
}

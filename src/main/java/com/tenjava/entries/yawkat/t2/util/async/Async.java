package com.tenjava.entries.yawkat.t2.util.async;

import java.util.function.Supplier;

/**
 * An async-sync API similar to waterfall.
 *
 * Usage:
 *
 * <p>
 * <code>
 * Async.pipeline(">")<br/>
 * .sync(str -> str + Thread.currentThread())<br/>
 * .async(str -> str + Thread.currentThread())<br/>
 * .sync(str -> str + Thread.currentThread())<br/>
 * .finishAsync(System.out::println);
 * </code>
 * </p>
 *
 * This would take a string ">", append the current thread name synchronously (main thread), append the next thread
 * name asynchronously (scheduler thread), append the next thread name synchronously (main thread again) and finally
 * print out the entire concatenated string asynchronously.
 *
 * @author yawkat
 */
public class Async {
    /**
     * Create a new pipeline with the given argument.
     */
    public static <T> Pipeline<T> pipeline(T argument) {
        return new InitPipeline<>(argument);
    }

    /**
     * Create a new pipeline with the given supplier as the first link. The supplier will be called synchronously.
     */
    public static <O> Pipeline<O> sync(Supplier<O> firstTask) {
        return pipeline(null).sync(obj -> firstTask.get());
    }

    /**
     * Create a new pipeline with the given supplier as the first link. The supplier will be called asynchronously.
     */
    public static <O> Pipeline<O> async(Supplier<O> firstTask) {
        return pipeline(null).async(obj -> firstTask.get());
    }
}

package com.tenjava.entries.yawkat.t2;

import java.util.ArrayDeque;
import java.util.Queue;
import org.bukkit.plugin.java.JavaPlugin;

public class TenJava extends JavaPlugin {
    /**
     * Tasks that were scheduled using #runTaskOnStartup before #onEnable was called.
     */
    private static final Queue<Runnable> startupTaskQueue = new ArrayDeque<>();

    private static TenJava instance;

    public static TenJava getInstance() {
        return instance;
    }

    /**
     * Run a task as soon as #instance is set (during startup). If #instance is already available, the task will be
     * executed immediately.
     */
    public static void runTaskOnStartup(Runnable task) {
        synchronized (startupTaskQueue) {
            if (getInstance() == null) {
                startupTaskQueue.offer(task);
            } else {
                task.run();
            }
        }
    }

    @Override
    public void onEnable() {
        synchronized (startupTaskQueue) {
            instance = this;
            // run any tasks that were scheduled
            while (!startupTaskQueue.isEmpty()) {
                runTaskOnStartup(startupTaskQueue.poll());
            }
        }
    }
}

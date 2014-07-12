package com.tenjava.entries.yawkat.t2;

import com.tenjava.entries.yawkat.t2.command.Commands;
import com.tenjava.entries.yawkat.t2.module.Module;
import com.tenjava.entries.yawkat.t2.module.TemperatureEnergyProducer;
import com.tenjava.entries.yawkat.t2.module.WalkEnergyProducer;
import com.tenjava.entries.yawkat.t2.persist.PersistedPlayerData;
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

        Commands.registerCommands();
        PersistedPlayerData.init();

        getServer().getPluginManager().registerEvents(new EnergyLoader(), this);

        Module.load(WalkEnergyProducer.class);
        Module.load(TemperatureEnergyProducer.class);
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}

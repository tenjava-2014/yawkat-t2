package com.tenjava.entries.yawkat.t2.command;

import com.tenjava.entries.yawkat.t2.TenJava;

/**
 * @author yawkat
 */
public class Commands {
    private Commands() {}

    public static void registerCommands() {
        TenJava.runTaskOnStartup(() -> {
            TenJava.getInstance().getCommand("power").setExecutor(new DisplayPowerCommand());
        });
    }
}

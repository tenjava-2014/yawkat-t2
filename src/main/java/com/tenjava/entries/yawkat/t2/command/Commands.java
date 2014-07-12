package com.tenjava.entries.yawkat.t2.command;

import com.tenjava.entries.yawkat.t2.TenJava;
import org.bukkit.ChatColor;

/**
 * @author yawkat
 */
public class Commands {
    static final String ERROR_PREFIX = ChatColor.YELLOW.toString() + ChatColor.ITALIC;

    private Commands() {}

    public static void registerCommands() {
        TenJava.runTaskOnStartup(() -> {
            TenJava.getInstance().getCommand("power").setExecutor(new DisplayPowerCommand());
            TenJava.getInstance().getCommand("poweradd").setExecutor(new PowerAddCommand());
        });
    }
}

package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.TenJava;
import org.bukkit.command.CommandExecutor;

/**
 * @author yawkat
 */
abstract class CommandModule extends Module implements CommandExecutor {
    private final String commandName;

    public CommandModule(String commandName) {
        this.commandName = commandName;
    }

    @Override
    protected void init() {
        super.init();
        TenJava.getInstance().getCommand(commandName).setExecutor(this);
    }
}

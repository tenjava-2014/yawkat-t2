package com.tenjava.entries.yawkat.t2.module;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Abstract class for EnergyAdd- and EnergyRemoveCommand that does the primary validation for them.
 *
 * @author yawkat
 */
abstract class EnergyChangeCommand extends CommandModule {
    public EnergyChangeCommand(String commandName) {
        super(commandName);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // check for args
        if (args.length != 2) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Usage: /" + command.getName() + " <name> <amount>");
            return true;
        }

        // get the target player
        String targetName = args[0];
        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(targetName);
        // not online
        if (target == null) {
            sender.sendMessage(Commands.ERROR_PREFIX + "No such player.");
            return true;
        }
        // get the amount
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            // invalid
            sender.sendMessage(Commands.ERROR_PREFIX + "Invalid amount.");
            return true;
        }

        // check for negative amount, NaN or +-INF (does parseDouble return those?)
        if (amount < 0 || !Double.isFinite(amount)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Amount must be positive.");
            return true;
        }

        perform(sender, target, amount);

        return true;
    }

    protected abstract void perform(CommandSender sender, Player target, double amount);
}

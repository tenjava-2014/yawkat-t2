package com.tenjava.entries.yawkat.t2.command;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author yawkat
 */
class EnergyAddCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Usage: /" + command.getAliases().get(0) + " <name> <amount>");
            return false;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(Commands.ERROR_PREFIX + "No such player.");
            return false;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Invalid amount.");
            return false;
        }

        if (amount < 0 || !Double.isFinite(amount)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Amount must be positive.");
            return false;
        }

        Energy.addEnergy(target, amount);
        sender.sendMessage(ChatColor.GOLD + "Added " +
                           ChatColor.AQUA + Commands.toDisplayString(amount) +
                           ChatColor.GOLD + " energy for " +
                           ChatColor.AQUA + target.getName() +
                           ChatColor.GOLD + ".");

        return true;
    }
}
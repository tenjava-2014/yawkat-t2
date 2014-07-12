package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to add energy to other players (or self).
 *
 * @author yawkat
 */
public class EnergyAddCommand extends EnergyChangeCommand {
    public EnergyAddCommand() {
        super("energyadd");
    }

    @Override
    protected void perform(CommandSender sender, Player target, double amount) {
        Energy.addEnergy(target, amount);
        sender.sendMessage(ChatColor.GOLD + "Added " +
                           ChatColor.AQUA + Commands.toDisplayString(amount) +
                           ChatColor.GOLD + " energy for " +
                           ChatColor.AQUA + target.getName() +
                           ChatColor.GOLD + ".");
    }
}

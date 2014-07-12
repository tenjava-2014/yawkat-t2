package com.tenjava.entries.yawkat.t2.command;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author yawkat
 */
class DisplayEnergyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof Player) {
            double energy = Energy.getEnergy((Player) sender);
            double rounded = Math.round(energy * 100) / 100D;
            sender.sendMessage(ChatColor.GOLD + "Your energy level is " +
                               ChatColor.AQUA + Commands.toDisplayString(rounded) +
                               ChatColor.GOLD + ".");
            return true;
        }
        return false;
    }
}

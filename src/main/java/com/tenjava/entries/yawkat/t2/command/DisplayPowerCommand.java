package com.tenjava.entries.yawkat.t2.command;

import com.tenjava.entries.yawkat.t2.Power;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author yawkat
 */
class DisplayPowerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof Player) {
            double power = Power.getPower((Player) sender);
            double rounded = Math.round(power * 100) / 100D;
            String roundedString;
            if ((int) rounded == rounded) {
                // if this is a round number don't print the trailing .0
                roundedString = String.valueOf((int) rounded);
            } else {
                roundedString = String.valueOf(rounded);
            }
            sender.sendMessage(ChatColor.GOLD + "Your power level is " +
                               ChatColor.AQUA + roundedString +
                               ChatColor.GOLD + ".");
            return true;
        }
        return false;
    }
}

package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to display own energy level.
 *
 * @author yawkat
 */
public class DisplayEnergyCommand extends CommandModule {
    public DisplayEnergyCommand() {
        super("energy");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        Player target;
        // player specified
        if (args.length >= 1) {
            //noinspection deprecation
            target = Bukkit.getPlayer(args[0]);
            // not online
            if (target == null) {
                sender.sendMessage(Commands.ERROR_PREFIX + "No such player.");
                return true;
            }
            if (!sender.equals(target)) {
                if (!sender.hasPermission("energy.display.other")) {
                    sender.sendMessage(Commands.ERROR_PREFIX + "You aren't allowed to display energy of other players");
                    return true;
                }
            } else {
                // ugly copied code from below
                if (!sender.hasPermission("energy.display.self")) {
                    sender.sendMessage(Commands.ERROR_PREFIX + "You aren't allowed to display your energy!");
                    return true;
                }
            }
        } else {
            // console
            if (!(sender instanceof Player)) {
                sender.sendMessage(Commands.ERROR_PREFIX + "Need to specify a player!");
                return true;
            }
            if (!sender.hasPermission("energy.display.self")) {
                sender.sendMessage(Commands.ERROR_PREFIX + "You aren't allowed to display your energy!");
                return true;
            }
            target = (Player) sender;
        }
        double energy = Energy.getEnergy(target);
        double rounded = Math.round(energy * 100) / 100D;
        // "your" if we are checking our own level
        sender.sendMessage((sender.equals(target) ? ChatColor.GOLD + "Your" :
                ChatColor.AQUA + target.getName() + ChatColor.GOLD + "'s")
                           + " energy level is " +
                           ChatColor.AQUA + Commands.toDisplayString(rounded) +
                           ChatColor.GOLD + ".");
        return true;
    }
}

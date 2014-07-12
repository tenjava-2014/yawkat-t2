package com.tenjava.entries.yawkat.t2.command;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * @author yawkat
 */
class StrikeCommand implements CommandExecutor {
    private static final double COST = 20;
    private static final int MAX_DISTANCE = 100;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Only players can cast lightning strikes.");
            return true;
        }

        Block target = ((LivingEntity) sender).getTargetBlock(null, MAX_DISTANCE);
        if (target == null) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Target is out of reach!");
            return true;
        }

        if (!Energy.deductEnergy((Player) sender, COST, Energy.DeductFailurePolicy.FAIL)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "You need at least " + Commands.toDisplayString(COST) +
                               " energy to do that!");
            return true;
        }

        target.getWorld().strikeLightning(target.getLocation());

        sender.sendMessage(ChatColor.GOLD + "Lightning cast for " +
                           ChatColor.AQUA + Commands.toDisplayString(COST) +
                           ChatColor.GOLD + " energy.");

        return true;
    }
}

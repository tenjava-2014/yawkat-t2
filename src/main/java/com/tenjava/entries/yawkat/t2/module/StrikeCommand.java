package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Casts a lightning strike at the place the player is looking at.
 *
 * @author yawkat
 */
public class StrikeCommand extends CommandModule {
    public StrikeCommand() {
        super("strike");
    }

    @Override
    protected void init() {
        super.init();
        getConfig().setDefault("max_distance", 100);
        getConfig().setDefault("cost", 20D);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Only players can cast lightning strikes.");
            return true;
        }

        // get target
        Block target = ((LivingEntity) sender).getTargetBlock(null, getConfig().get("max_distance"));
        if (target == null) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Target is out of reach!");
            return true;
        }

        // remove energy
        if (!Energy.deductEnergy((Player) sender, getConfig().get("cost"), Energy.DeductFailurePolicy.FAIL)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "You need at least " +
                               Commands.toDisplayString(getConfig().get("cost")) + " energy to do that!");
            return true;
        }

        // cast
        target.getWorld().strikeLightning(target.getLocation());

        sender.sendMessage(ChatColor.GOLD + "Lightning cast for " +
                           ChatColor.AQUA + Commands.toDisplayString(getConfig().get("cost")) +
                           ChatColor.GOLD + " energy.");

        return true;
    }
}

package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;

/**
 * @author yawkat
 */
public class FurnaceChargeCommand extends CommandModule {
    public FurnaceChargeCommand() {
        super("charge");
    }

    @Override
    protected void init() {
        super.init();
        getConfig().setDefault("burn_per_energy", 20D);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Only players can charge furnaces.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Usage: /" + command.getName() + " <energy amount>");
            return true;
        }

        double energy;
        try {
            energy = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            // invalid
            sender.sendMessage(Commands.ERROR_PREFIX + "Invalid amount.");
            return true;
        }

        // check for negative amount, NaN or +-INF (does parseDouble return those?)
        if (energy < 0 || !Double.isFinite(energy)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Amount must be positive.");
            return true;
        }

        if (!Energy.deductEnergy((Player) sender, energy, Energy.DeductFailurePolicy.FAIL)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Not enough energy!");
            return true;
        }

        Block lookingAt = ((LivingEntity) sender).getTargetBlock(null, 4);
        if (lookingAt == null || lookingAt.getType() != Material.FURNACE) {
            sender.sendMessage(Commands.ERROR_PREFIX + "You need to look at a furnace!");
            return true;
        }

        Furnace furnace = (Furnace) lookingAt.getState();
        // rounding down here to avoid exploiting with small energy values
        furnace.setBurnTime((short) (furnace.getBurnTime() + energy * getConfig().<Double>get("burn_per_energy")));

        sender.sendMessage(ChatColor.GOLD + "Furnace charged!");

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Module.getModule(Battery.class).ifPresent(battery -> {
            if (event.getInventory().getType() == InventoryType.FURNACE) {
                if (battery.isBattery(event.getCursor())) {
                    double charge = battery.getCharge(event.getCursor());
                    event.setCancelled(true);
                    event.setCursor(null);
                    if (event.getWhoClicked() instanceof Player) {
                        ((Player) event.getWhoClicked()).updateInventory();
                    }
                    Furnace holder = ((FurnaceInventory) event.getInventory()).getHolder();
                    holder.setBurnTime((short) (holder.getBurnTime() +
                                                charge * getConfig().<Double>get("burn_per_energy")));
                }
            }
        });
    }
}

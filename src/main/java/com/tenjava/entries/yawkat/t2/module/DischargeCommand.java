package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Discharge energy into the environment around the player, damaging and blinding entities.
 *
 * @author yawkat
 */
public class DischargeCommand extends CommandModule {
    public DischargeCommand() {
        super("discharge");
    }

    @Override
    protected void init() {
        super.init();
        // Range / energy; if 20 energy is spent maximum range is 10 blocks
        getConfig().setDefault("max_range_per_unit", 0.5);
        // Minimum energy required to discharge
        getConfig().setDefault("min_energy", 1D);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Only players can discharge energy.");
            return true;
        }

        // IntelliJ made me make this variable final even though it doesn't have to be :(
        @SuppressWarnings("UnnecessaryFinalOnLocalVariableOrParameter")
        final double energy;
        if (args.length >= 1) {
            // amount specified
            try {
                energy = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                // invalid
                sender.sendMessage(Commands.ERROR_PREFIX + "Invalid amount.");
                return true;
            }
            // check for negative amount, NaN or +-INF
            if (energy < 0 || !Double.isFinite(energy)) {
                sender.sendMessage(Commands.ERROR_PREFIX + "Amount must be positive.");
                return true;
            }
        } else {
            // use up all energy
            energy = Energy.getEnergy((Player) sender);
        }

        // check for minimum
        if (energy < getConfig().<Double>get("min_energy")) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Minimum energy is " +
                               Commands.toDisplayString(getConfig().get("min_energy")) + "!");
            return true;
        }

        // remove energy (fails if too much is specified in args[0])
        if (!Energy.deductEnergy((Player) sender, energy, Energy.DeductFailurePolicy.FAIL)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Not enough energy!");
            return true;
        }
        discharge((Player) sender, energy);

        return true;
    }

    private void discharge(Player on, double energy) {// range of the discharge
        double range = energy * getConfig().<Double>get("max_range_per_unit");
        on.getNearbyEntities(range, range, range).forEach(entity -> {
            double distance = entity.getLocation().distance(((Entity) on).getLocation());
            // force of the electric shock (0-energy)
            double force = (1 - distance / range) * energy;
            if (force < 0) {
                // distance > range
                return;
            }

            // resistance from negative effects (damage, blindness)
            // 1 = no resistance 0 = full resistance (no damage)
            double resistance = 1;
            if (entity instanceof Player) {
                // store some additional energy in that player (0-force/2 to prevent loops)
                Energy.addEnergy((Player) entity, force / 2);
                // -0.25 resistance for each iron armor part
                resistance -= IronArmorEnergyConsumer.getIronArmorCount((HumanEntity) entity) * 0.25;
            }
            if (entity instanceof Damageable) {
                // apply damage (0-energy half hearts), shrinks with range
                ((Damageable) entity).damage(force * resistance, (Entity) on);
            }
            if (entity instanceof LivingEntity) {
                // apply blindness (0-energy seconds), shrinks with range
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                                                                         (int) (force * resistance * 20),
                                                                         1));
            }
        });

        double roundEnergy = Math.round(energy * 100) / 100;
        on.sendMessage(ChatColor.GOLD + "Discharged " +
                       ChatColor.AQUA + Commands.toDisplayString(roundEnergy) +
                       ChatColor.GOLD + " energy!");
    }

    @EventHandler
    public void onUseItem(PlayerInteractEvent event) {
        Module.getModule(Battery.class).ifPresent(battery -> {
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                battery.isBattery(event.getItem())) {
                double charge = battery.getCharge(event.getItem());
                // deplete
                event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), null);
                event.setCancelled(true);
                discharge(event.getPlayer(), charge);
            }
        });
    }
}

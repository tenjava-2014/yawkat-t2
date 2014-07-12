package com.tenjava.entries.yawkat.t2.command;

import com.tenjava.entries.yawkat.t2.Energy;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Discharge energy into the environment around the player, damaging and blinding entities.
 *
 * @author yawkat
 */
class DischargeCommand implements CommandExecutor {
    /**
     * Minimum energy required to discharge.
     */
    private static final double MIN_ENERGY = 1;
    /**
     * Range / energy; if 20 energy is spent maximum range is 10 blocks.
     */
    private static final double MAX_RANGE_PER_UNIT = 0.5;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Only players can discharge energy.");
            return true;
        }

        double energy;
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
        if (energy < MIN_ENERGY) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Minimum energy is " +
                               Commands.toDisplayString(MIN_ENERGY) + "!");
            return true;
        }

        // remove energy (fails if too much is specified in args[0])
        if (!Energy.deductEnergy((Player) sender, energy, Energy.DeductFailurePolicy.FAIL)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Not enough energy!");
            return true;
        }

        // range of the discharge
        double range = energy * MAX_RANGE_PER_UNIT;
        ((Entity) sender).getNearbyEntities(range, range, range).forEach(entity -> {
            double distance = entity.getLocation().distance(((Entity) sender).getLocation());
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
                resistance -= Arrays.stream(((HumanEntity) entity).getInventory().getArmorContents())
                        // find armor
                                      .filter(stack -> {
                    if (stack == null) {
                        return false;
                    }
                    Material type = stack.getType();
                    return type == Material.IRON_BOOTS ||
                           type == Material.IRON_HELMET ||
                           type == Material.IRON_CHESTPLATE ||
                           type == Material.IRON_LEGGINGS ||
                           type == Material.IRON_BLOCK ||
                           type == Material.IRON_INGOT;
                }).count() * 0.25;
            }
            if (entity instanceof Damageable) {
                // apply damage (0-energy half hearts), shrinks with range
                ((Damageable) entity).damage(force * resistance, (Entity) sender);
            }
            if (entity instanceof LivingEntity) {
                // apply blindness (0-energy seconds), shrinks with range
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                                                                         (int) (force * resistance * 20),
                                                                         1));
            }
        });

        return true;
    }
}

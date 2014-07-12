package com.tenjava.entries.yawkat.t2.command;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

            if (entity instanceof Damageable) {
                // apply damage (0-energy half hearts), shrinks with range
                ((Damageable) entity).damage(force, (Entity) sender);
            }
            if (entity instanceof LivingEntity) {
                // apply blindness (0-energy seconds), shrinks with range
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                                                                         (int) force * 20,
                                                                         1));
            }
            if (entity instanceof Player) {
                // store some additional energy in that player (0-force/2 to prevent loops)
                Energy.addEnergy((Player) entity, force / 2);
            }
        });

        return true;
    }
}

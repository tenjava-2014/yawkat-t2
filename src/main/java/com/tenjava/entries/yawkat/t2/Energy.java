package com.tenjava.entries.yawkat.t2;

import com.google.common.base.Preconditions;
import com.tenjava.entries.yawkat.t2.util.player.DataDescriptor;
import com.tenjava.entries.yawkat.t2.util.player.PlayerData;
import org.bukkit.entity.Player;

/**
 * Utility class that stores energy levels for individual players.
 *
 * @author yawkat
 */
public class Energy {
    /**
     * DataDescriptor for our energy field.
     */
    private static final DataDescriptor<Double> PLAYER_ENERGY = new DataDescriptor<>(0D);

    /**
     * Get the energy a player currently has in their body.
     */
    public static synchronized double getEnergy(Player player) {
        Preconditions.checkNotNull(player, "player");

        return PlayerData.forPlayer(player).get(PLAYER_ENERGY);
    }

    /**
     * Set the energy a player has in their body.
     */
    public static synchronized void setEnergy(Player player, double energy) {
        Preconditions.checkNotNull(player, "player");

        PlayerData.forPlayer(player).set(PLAYER_ENERGY, energy);
    }

    /**
     * Add energy to a player.
     *
     * @param added the energy that was added; must be positive.
     */
    public static synchronized void addEnergy(Player player, double added) {
        Preconditions.checkArgument(added >= 0, "Cannot add negative amount of energy");

        changeEnergy(player, +added);
    }

    /**
     * Remove energy from a player.
     *
     * @param removed the energy that was removed; must be positive.
     * @return The amount of energy that was <i>actually</i> deducted: If a player has less than the given energy, only
     * their remaining energy will be used up.
     */
    public static synchronized double deductEnergy(Player player, double removed) {
        Preconditions.checkArgument(removed >= 0, "Cannot remove negative amount of energy");

        return -changeEnergy(player, -removed);
    }

    /**
     * Remove energy from a player with a given failurePolicy.
     *
     * @param failurePolicy what to do on failure (not enough energy).
     * @return If the deduction was successful according to our failurePolicy.
     */
    public static synchronized boolean deductEnergy(Player player, double removed, DeductFailurePolicy failurePolicy) {
        Preconditions.checkNotNull(failurePolicy, "failurePolicy");

        // check for enough energy
        if (getEnergy(player) < removed && failurePolicy == DeductFailurePolicy.FAIL) {
            return false;
        }
        return Math.abs(deductEnergy(player, removed) - removed) < 0.00001;
    }

    private static double changeEnergy(Player player, double delta) {
        double previous = getEnergy(player);
        double now = Math.max(0, previous + delta);
        setEnergy(player, now);
        return now - previous;
    }

    /**
     * What to do when #deductEnergy fails.
     */
    public static enum DeductFailurePolicy {
        /**
         * Return false on failure, not changing the player's energy level.
         */
        FAIL,
        /**
         * Use up the remaining energy of the player and return whether he did have any energy at all.
         */
        USE_UP,
    }
}

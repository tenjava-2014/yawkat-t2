package com.tenjava.entries.yawkat.t2;

import com.google.common.base.Preconditions;
import com.tenjava.entries.yawkat.t2.util.player.DataDescriptor;
import com.tenjava.entries.yawkat.t2.util.player.PlayerData;
import org.bukkit.entity.Player;

/**
 * Utility class that stores power levels for individual players.
 *
 * @author yawkat
 */
public class Power {
    /**
     * DataDescriptor for our power field.
     */
    private static final DataDescriptor<Double> PLAYER_POWER = new DataDescriptor<>(0D);

    /**
     * Get the power a player currently has in their body.
     */
    public static synchronized double getPower(Player player) {
        Preconditions.checkNotNull(player, "player");

        return PlayerData.forPlayer(player).get(PLAYER_POWER);
    }

    /**
     * Set the power a player has in their body.
     */
    public static synchronized void setPower(Player player, double power) {
        Preconditions.checkNotNull(player, "player");

        PlayerData.forPlayer(player).set(PLAYER_POWER, power);
    }

    /**
     * Add power to a player.
     *
     * @param added the power that was added; must be positive.
     */
    public static synchronized void addPower(Player player, double added) {
        Preconditions.checkArgument(added >= 0, "Cannot add negative amount of power");

        changePower(player, +added);
    }

    /**
     * Remove power from a player.
     *
     * @param removed the power that was removed; must be positive.
     * @return The amount of power that was <i>actually</i> deducted: If a player has less than the given power, only
     * their remaining power will be used up.
     */
    public static synchronized double deductPower(Player player, double removed) {
        Preconditions.checkArgument(removed >= 0, "Cannot remove negative amount of power");

        return -changePower(player, -removed);
    }

    /**
     * Remove power from a player with a given failurePolicy.
     *
     * @param failurePolicy what to do on failure (not enough power).
     * @return If the deduction was successful according to our failurePolicy.
     */
    public static synchronized boolean deductPower(Player player, double removed, DeductFailurePolicy failurePolicy) {
        Preconditions.checkNotNull(failurePolicy, "failurePolicy");

        // check for enough power
        if (getPower(player) < removed && failurePolicy == DeductFailurePolicy.FAIL) {
            return false;
        }
        return Math.abs(deductPower(player, removed) - removed) < 0.00001;
    }

    private static double changePower(Player player, double delta) {
        double previous = getPower(player);
        double now = Math.max(0, previous + delta);
        setPower(player, now);
        return now - previous;
    }

    /**
     * What to do when #deductPower fails.
     */
    public static enum DeductFailurePolicy {
        /**
         * Return false on failure, not changing the player's power level.
         */
        FAIL,
        /**
         * Use up the remaining power of the player and return whether he did have any power at all.
         */
        USE_UP,
    }
}

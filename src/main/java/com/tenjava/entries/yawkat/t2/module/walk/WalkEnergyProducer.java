package com.tenjava.entries.yawkat.t2.module.walk;

import com.tenjava.entries.yawkat.t2.Energy;
import com.tenjava.entries.yawkat.t2.module.Module;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

/**
 * Module that produces a small amount of energy when a player travels.
 *
 * @author yawkat
 */
public class WalkEnergyProducer extends Module {
    /**
     * How much energy is earned from walking one block
     */
    private static final double DISTANCE_ENERGY_MULTIPLIER = 0.01;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event instanceof PlayerTeleportEvent) {
            // don't count teleports
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (!from.getWorld().equals(to.getWorld())) {
            // we can't reliably handle different worlds
            return;
        }
        // direction vector we walked along
        Vector off = from.toVector().subtract(to.toVector());
        // horizontal distance
        double dist = Math.sqrt(off.getX() * off.getX() + off.getZ() * off.getZ());
        double gain = dist * DISTANCE_ENERGY_MULTIPLIER;
        // increase the players energy level
        Energy.addEnergy(event.getPlayer(), gain);
    }
}

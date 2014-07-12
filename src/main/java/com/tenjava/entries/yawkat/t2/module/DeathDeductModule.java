package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Remove some energy from a player on death.
 *
 * @author yawkat
 */
public class DeathDeductModule extends Module {
    @Override
    protected void init() {
        super.init();
        // how much of the energy remains after death
        getConfig().setDefault("factor", 0.5);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        double energy = Energy.getEnergy(event.getEntity());
        // calc new energy
        double newEnergy = energy * getConfig().<Double>get("factor");
        // set the energy for the player
        Energy.setEnergy(event.getEntity(), newEnergy);
    }
}

package com.tenjava.entries.yawkat.t2;

import com.tenjava.entries.yawkat.t2.persist.PersistedPlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * @author yawkat
 */
public class EnergyLoader implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        Energy.setEnergy(event.getPlayer(), PersistedPlayerData.get(event.getPlayer()).get("energy", 0D));
    }

    @EventHandler
    public void onEnergyChange(EnergyChangeEvent event) {
        PersistedPlayerData.get(event.getPlayer()).set("energy", Energy.getEnergy(event.getPlayer()));
    }
}

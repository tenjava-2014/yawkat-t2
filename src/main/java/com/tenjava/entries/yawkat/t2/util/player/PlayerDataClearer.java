package com.tenjava.entries.yawkat.t2.util.player;

import com.tenjava.entries.yawkat.t2.TenJava;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener that ensures all players that log out have their PlayerData reset.
 *
 * @author yawkat
 */
class PlayerDataClearer implements Listener {
    static void register() {
        // register our data clearer as soon as possible
        TenJava.runTaskOnStartup(() -> Bukkit.getPluginManager().registerEvents(new PlayerDataClearer(),
                                                                                TenJava.getInstance()));
    }

    // use monitor here so we can be relatively sure that all things done in other quit handlers are over.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogout(PlayerQuitEvent event) {
        PlayerData.clearPlayer(event.getPlayer());
    }
}

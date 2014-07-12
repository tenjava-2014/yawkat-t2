package com.tenjava.entries.yawkat.t2.persist;

import com.tenjava.entries.yawkat.t2.TenJava;
import com.tenjava.entries.yawkat.t2.util.async.Async;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author yawkat
 */
class PersistedPlayerDataListener implements Listener {
    static void register() {
        TenJava.runTaskOnStartup(() -> Bukkit.getPluginManager().registerEvents(new PersistedPlayerDataListener(),
                                                                                TenJava.getInstance()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        PersistedPlayerData.preload(event.getUniqueId());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        Async.pipeline(event.getPlayer().getUniqueId())
                .finishAsync(PersistedPlayerData::unload);
    }
}

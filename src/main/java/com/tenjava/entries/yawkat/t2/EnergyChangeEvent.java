package com.tenjava.entries.yawkat.t2;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author yawkat
 */
public class EnergyChangeEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final double previousEnergy;

    public EnergyChangeEvent(Player who, double previousEnergy) {
        super(who);
        this.previousEnergy = previousEnergy;
    }

    public double getPreviousEnergy() {
        return previousEnergy;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

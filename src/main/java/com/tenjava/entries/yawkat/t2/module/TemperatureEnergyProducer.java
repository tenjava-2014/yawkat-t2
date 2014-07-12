package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import com.tenjava.entries.yawkat.t2.TenJava;
import com.tenjava.entries.yawkat.t2.util.player.DataDescriptor;
import com.tenjava.entries.yawkat.t2.util.player.PlayerData;
import java.util.Arrays;
import java.util.OptionalDouble;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Produces energy when the temperature of the player's environment <i>changes</i> (fire, lighting, biome).
 *
 * @author yawkat
 */
public class TemperatureEnergyProducer extends Module {
    /**
     * OptionalDouble of the temperature that the player was in last update. Defaults to empty so we players can't
     * produce energy by logging in and out again.
     */
    private static final DataDescriptor<OptionalDouble> HEAT = new DataDescriptor<>(OptionalDouble.empty());
    /**
     * Ticks since the last heat update. When this hits 20 (1 sec), an update is performed.
     */
    private static final DataDescriptor<Integer> LAST_HEAT_UPDATE = new DataDescriptor<>(0);

    /**
     * How far we should scan for firey blocks
     */
    private static final int BLOCK_SCAN_RADIUS = 5;

    /**
     * How much the light level should contribute to the heat value.
     */
    private static final double LIGHT_LEVEL_MULTIPLIER = 0.02;
    /**
     * How much the biome temperature should contribute to the heat value.
     */
    private static final double BIOME_TEMP_MULTIPLIER = 0.01;
    /**
     * How much the block-based temperature (lava, fire) should contribute to the heat value.
     */
    private static final double BLOCK_TEMP_MULTIPLIER = 0.1;
    /**
     * Multiplier for energy gained through heat.
     */
    private static final double ENERGY_GAIN_MULTIPLIER = 0.5;

    @Override
    protected void init() {
        super.init();
        Bukkit.getScheduler().runTaskTimer(TenJava.getInstance(), this::updateHeat, 1, 1);
    }

    private double calculateHeat(Player player) {
        // block the player is at
        Block block = player.getLocation().getBlock();
        // light level (0-15)
        byte lightLevel = block.getLightLevel();
        // biome temperature (0-1?)
        double biomeTemperature = block.getTemperature();
        // distance-weighted count of nearby hot blocks
        double hotBlocksInVicinityWeighted = 0;
        for (int x = -BLOCK_SCAN_RADIUS; x <= BLOCK_SCAN_RADIUS; x++) {
            for (int y = -BLOCK_SCAN_RADIUS; y <= BLOCK_SCAN_RADIUS; y++) {
                for (int z = -BLOCK_SCAN_RADIUS; z <= BLOCK_SCAN_RADIUS; z++) {
                    Block there = block.getRelative(x, y, z);
                    Material type = there.getType();
                    if (type == Material.LAVA || type == Material.STATIONARY_LAVA || type == Material.FIRE) {
                        // assuming the "heat collector" is at +1 of the players feet we need to add 1.5 to y to get
                        // the correct distance
                        double dist = there.getLocation().add(0.5, 1.5, 0.5).distance(player.getLocation());
                        // weight is radius / distance because that worked well
                        hotBlocksInVicinityWeighted += 1 / dist / BLOCK_SCAN_RADIUS;
                    }
                }
            }
        }

        // calculate total heat
        double heat = 0;
        // ll is 1-15 so divide it by that
        heat += lightLevel / 15D * LIGHT_LEVEL_MULTIPLIER;
        heat += biomeTemperature * BIOME_TEMP_MULTIPLIER;
        heat += hotBlocksInVicinityWeighted * BLOCK_TEMP_MULTIPLIER;
        return heat;
    }

    private void doUpdateHeat(Player player) {
        // current heat
        double heatNow = calculateHeat(player);
        // heat on last update
        double heatPrev = PlayerData.forPlayer(player).get(HEAT).orElse(heatNow);
        // heat difference
        double heatOff = Math.abs(heatPrev - heatNow);
        // how much energy was gained from that
        double energyGain = heatOff * ENERGY_GAIN_MULTIPLIER;
        Energy.addEnergy(player, energyGain);
        // save heat for next update
        PlayerData.forPlayer(player).set(HEAT, OptionalDouble.of(heatNow));
    }

    private void updateHeat() {
        // update all players where lastUpdate is >= 20. We're not updating everyone at once to try to avoid lag
        // spikes caused by block scanning a lot of players.
        Arrays.stream(Bukkit.getOnlinePlayers()).forEach(player -> {
            int lastUpdate = PlayerData.forPlayer(player).get(LAST_HEAT_UPDATE);
            if (lastUpdate >= 20) {
                // update
                lastUpdate = 0;
                doUpdateHeat(player);
            } else {
                // wait
                lastUpdate++;
            }
            PlayerData.forPlayer(player).set(LAST_HEAT_UPDATE, lastUpdate);
        });
    }
}

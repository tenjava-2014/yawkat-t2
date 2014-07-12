package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import com.tenjava.entries.yawkat.t2.TenJava;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

/**
 * When wearing iron armor, energy is lost every second.
 *
 * @author yawkat
 */
public class IronArmorEnergyConsumer extends Module {
    @Override
    protected void init() {
        super.init();
        // How much energy should be deducted from the player per second and iron armor item
        getConfig().setDefault("armor_deduction_per_second", 0.002);
        Bukkit.getScheduler().runTaskTimer(TenJava.getInstance(), this::updateArmor, 20, 20);
    }

    private void updateArmor() {
        Arrays.stream(Bukkit.getOnlinePlayers()).forEach(player -> {
            long ironArmorCount = getIronArmorCount(player);
            if (ironArmorCount > 0) {
                double deduct = ironArmorCount * getConfig().<Double>get("armor_deduction_per_second");
                Energy.deductEnergy(player, deduct, Energy.DeductFailurePolicy.USE_UP);
            }
        });
    }

    /**
     * Get the count of iron armor items on a player.
     */
    public static long getIronArmorCount(HumanEntity entity) {
        return Arrays.stream(entity.getInventory().getArmorContents())
                // find armor
                .filter(stack -> {
                    if (stack == null) {
                        return false;
                    }
                    Material type = stack.getType();
                    return type == Material.IRON_BOOTS ||
                           type == Material.IRON_HELMET ||
                           type == Material.IRON_CHESTPLATE ||
                           type == Material.IRON_LEGGINGS ||
                           type == Material.IRON_BLOCK ||
                           type == Material.IRON_INGOT;
                    // count
                }).count();
    }
}

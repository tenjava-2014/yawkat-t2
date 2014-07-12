package com.tenjava.entries.yawkat.t2.module;

import com.tenjava.entries.yawkat.t2.Energy;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Adds batteries that can be created via /battery &lt;charge&gt; and can be used by putting them into the helmet slot.
 *
 * @author yawkat
 */
public class Battery extends CommandModule {
    private static final Pattern DOUBLE = Pattern.compile("[0-9]+(\\.[0-9]*)?");

    public Battery() {
        super("battery");
    }

    @Override
    protected void init() {
        super.init();

        getConfig().setDefault("material", Material.BLAZE_POWDER.toString());
        getConfig().setDefault("name", ChatColor.AQUA + "Battery (Charge: %.2f)");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        // click helmet slot
        if (isBattery(event.getCursor()) &&
            event.getInventory().getType() == InventoryType.CRAFTING &&
            event.getSlot() == 39) {

            double charge = getCharge(event.getCursor());
            // cancel event
            event.setCancelled(true);
            // remove batters
            event.setCursor(null);
            player.updateInventory();
            // add energy
            Energy.addEnergy(player, charge);
            // send confirmation
            player.sendMessage(ChatColor.GOLD + "Added " +
                               ChatColor.AQUA + Commands.toDisplayString(Math.round(charge * 100) / 100D) +
                               ChatColor.GOLD + " energy.");
        }
    }

    /**
     * Returns whether a stack is a valid battery.
     */
    private boolean isBattery(ItemStack stack) {
        // air
        if (stack == null) {
            return false;
        }
        // type
        if (!stack.getType().toString().equalsIgnoreCase(getConfig().get("material"))) {
            return false;
        }
        // charge = 0 => normal item
        if (getCharge(stack) == 0) {
            return false;
        }
        return true;
    }

    /**
     * Get the charge of a battery stack.
     */
    private double getCharge(ItemStack stack) {
        // wow this is hacky
        Matcher m = DOUBLE.matcher(stack.getItemMeta().getDisplayName());
        if (!m.find()) {
            return 0;
        }
        return Double.parseDouble(m.group()) * stack.getAmount();
    }

    /**
     * Create a battery of the given charge. Returns an empty optional if the charge is too low.
     */
    private Optional<ItemStack> createBattery(double charge) {
        ItemStack stack = new ItemStack(Material.getMaterial(getConfig().<String>get("material")));
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(String.format(getConfig().get("name"), charge));
        stack.setItemMeta(meta);
        // if charge = 0 we need to return empty because it would be counted as a normal item
        return getCharge(stack) == 0 ? Optional.empty() : Optional.of(stack);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Only players can create batteries.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Usage: /" + command.getName() + " <charge>");
            return true;
        }

        double energy;
        try {
            energy = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            // invalid
            sender.sendMessage(Commands.ERROR_PREFIX + "Invalid amount.");
            return true;
        }

        // check for negative amount, NaN or +-INF (does parseDouble return those?)
        if (energy < 0 || !Double.isFinite(energy)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Amount must be positive.");
            return true;
        }

        Optional<ItemStack> stack = createBattery(energy);

        if (!stack.isPresent()) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Charge too small!");
            return false;
        }

        if (!Energy.deductEnergy((Player) sender, energy, Energy.DeductFailurePolicy.FAIL)) {
            sender.sendMessage(Commands.ERROR_PREFIX + "Not enough energy!");
            return true;
        }

        ((InventoryHolder) sender).getInventory().addItem(stack.get());

        sender.sendMessage(ChatColor.GOLD + "Battery created!");

        return true;
    }
}

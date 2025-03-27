package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ConfigMenu implements InventoryHolder {
    private final Inventory inv;

    public ConfigMenu() {
        inv = Bukkit.createInventory(this, 27, "Configuration PushOut");
        init();
    }

    private void init() {
        inv.clear();
        // Option 1 : KB_MULTIPLIER
        inv.setItem(10, createOptionItem("KB Multiplier", Config.KB_MULTIPLIER + ""));
        inv.setItem(11, createButton(Material.EMERALD, "Increase KB Multiplier"));
        inv.setItem(12, createButton(Material.REDSTONE, "Decrease KB Multiplier"));

        // Option 2 : KB_BASE
        inv.setItem(13, createOptionItem("KB Base", Config.KB_BASE + ""));
        inv.setItem(14, createButton(Material.EMERALD, "Increase KB Base"));
        inv.setItem(15, createButton(Material.REDSTONE, "Decrease KB Base"));

        // Option 3 : Jump Boost II activé/désactivé
        inv.setItem(19, createOptionItem("Jump Boost II", Config.JUMP_BOOST_ENABLED ? "Enabled" : "Disabled"));
        inv.setItem(20, createButton(Material.EMERALD, "Enable Jump Boost II"));
        inv.setItem(21, createButton(Material.REDSTONE, "Disable Jump Boost II"));

        // Option 4 : Hit % Gain
        inv.setItem(22, createOptionItem("Hit % Gain", Config.HIT_PERCENT_GAIN + ""));
        inv.setItem(23, createButton(Material.EMERALD, "Increase Hit % Gain"));
        inv.setItem(24, createButton(Material.REDSTONE, "Decrease Hit % Gain"));
    }

    private ItemStack createOptionItem(String name, String value) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name);
        meta.setLore(Arrays.asList(ChatColor.AQUA + "Current value: " + ChatColor.GREEN + value));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}

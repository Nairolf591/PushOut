package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        // Option 7 : Activation des objets (Utilisation des slots 0, 7 et 8)
        inv.setItem(0, createButton(Material.REDSTONE, "Désactiver les objets"));
        inv.setItem(7, createOptionItem("Objets activés", Config.OBJECTS_ENABLED ? "Activé" : "Désactivé"));
        inv.setItem(8, createButton(Material.EMERALD, "Activer les objets"));

        // Option 5 : Force du grappin (slots 1-3)
        inv.setItem(1, createOptionItem("Force du grappin", Config.GRAPPLING_FORCE + ""));
        inv.setItem(2, createButton(Material.EMERALD, "Augmenter la force du grappin"));
        inv.setItem(3, createButton(Material.REDSTONE, "Diminuer la force du grappin"));

        // Option 6 : Rayon de TP (slots 4-6)
        inv.setItem(4, createOptionItem("Rayon de TP", Config.TP_RADIUS + ""));
        inv.setItem(5, createButton(Material.EMERALD, "Augmenter le rayon de TP"));
        inv.setItem(6, createButton(Material.REDSTONE, "Diminuer le rayon de TP"));

        // Option 8 : Rayon maximum de la map (slots 9, 16 et 17)
        inv.setItem(9, createOptionItem("Rayon max de la map", Config.MAP_MAX_RADIUS + ""));
        inv.setItem(16, createButton(Material.EMERALD, "Augmenter le rayon max"));
        inv.setItem(17, createButton(Material.REDSTONE, "Diminuer le rayon max"));

        // Option 1 : Multiplicateur KB (slots 10-12)
        inv.setItem(10, createOptionItem("Multiplicateur KB", Config.KB_MULTIPLIER + ""));
        inv.setItem(11, createButton(Material.EMERALD, "Augmenter le multiplicateur KB"));
        inv.setItem(12, createButton(Material.REDSTONE, "Diminuer le multiplicateur KB"));

        // Option 2 : Base KB (slots 13-15)
        inv.setItem(13, createOptionItem("Base KB", Config.KB_BASE + ""));
        inv.setItem(14, createButton(Material.EMERALD, "Augmenter la base KB"));
        inv.setItem(15, createButton(Material.REDSTONE, "Diminuer la base KB"));

        // Option 3 : Jump Boost II (slots 19-21)
        inv.setItem(19, createOptionItem("Jump Boost II", Config.JUMP_BOOST_ENABLED ? "Activé" : "Désactivé"));
        inv.setItem(20, createButton(Material.EMERALD, "Activer Jump Boost II"));
        inv.setItem(21, createButton(Material.REDSTONE, "Désactiver Jump Boost II"));

        // Option 4 : Gain % par coup (slots 22-24)
        inv.setItem(22, createOptionItem("Gain % par coup", Config.HIT_PERCENT_GAIN + ""));
        inv.setItem(23, createButton(Material.EMERALD, "Augmenter le gain % par coup"));
        inv.setItem(24, createButton(Material.REDSTONE, "Diminuer le gain % par coup"));
    }

    private ItemStack createOptionItem(String nom, String valeur) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + nom);
        meta.setLore(Arrays.asList(ChatColor.AQUA + "Valeur actuelle : " + ChatColor.GREEN + valeur));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createButton(Material materiau, String nom) {
        ItemStack item = new ItemStack(materiau);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + nom);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}

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
        // Option Colonne 0 : Objets (boolean)
        inv.setItem(0, createButton(Material.REDSTONE, "Désactiver les objets"));
        inv.setItem(9, createOptionItem("Objets", Config.OBJECTS_ENABLED ? "Activés" : "Désactivés"));
        inv.setItem(18, createButton(Material.EMERALD, "Activer les objets"));

        // Option Colonne 1 : Force du grappin (double)
        inv.setItem(1, createButton(Material.REDSTONE, "Diminuer la force du grappin"));
        inv.setItem(10, createOptionItem("Force du grappin", Config.GRAPPLING_FORCE + ""));
        inv.setItem(19, createButton(Material.EMERALD, "Augmenter la force du grappin"));

        // Option Colonne 2 : Rayon de TP (double)
        inv.setItem(2, createButton(Material.REDSTONE, "Diminuer le rayon de TP"));
        inv.setItem(11, createOptionItem("Rayon de TP", Config.TP_RADIUS + ""));
        inv.setItem(20, createButton(Material.EMERALD, "Augmenter le rayon de TP"));

        // Option Colonne 3 : Rayon max de la map (double)
        inv.setItem(3, createButton(Material.REDSTONE, "Diminuer le rayon max"));
        inv.setItem(12, createOptionItem("Rayon max de la map", Config.MAP_MAX_RADIUS + ""));
        inv.setItem(21, createButton(Material.EMERALD, "Augmenter le rayon max"));

        // Option Colonne 4 : Multiplicateur KB (double)
        inv.setItem(4, createButton(Material.REDSTONE, "Diminuer le multiplicateur KB"));
        inv.setItem(13, createOptionItem("Multiplicateur KB", Config.KB_MULTIPLIER + ""));
        inv.setItem(22, createButton(Material.EMERALD, "Augmenter le multiplicateur KB"));

        // Option Colonne 5 : Base KB (double)
        inv.setItem(5, createButton(Material.REDSTONE, "Diminuer la base KB"));
        inv.setItem(14, createOptionItem("Base KB", Config.KB_BASE + ""));
        inv.setItem(23, createButton(Material.EMERALD, "Augmenter la base KB"));

        // Option Colonne 6 : Jump Boost II (boolean)
        inv.setItem(6, createButton(Material.REDSTONE, "Désactiver Jump Boost II"));
        inv.setItem(15, createOptionItem("Jump Boost II", Config.JUMP_BOOST_ENABLED ? "Activé" : "Désactivé"));
        inv.setItem(24, createButton(Material.EMERALD, "Activer Jump Boost II"));

        // Option Colonne 7 : Gain % par coup (double)
        inv.setItem(7, createButton(Material.REDSTONE, "Diminuer le gain % par coup"));
        inv.setItem(16, createOptionItem("Gain % par coup", Config.HIT_PERCENT_GAIN + ""));
        inv.setItem(25, createButton(Material.EMERALD, "Augmenter le gain % par coup"));
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

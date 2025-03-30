package me.pushout;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ConfigMenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConfigMenu)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        switch (slot) {
            // Colonne 0 : Objets
            case 0: // Désactiver les objets
                Config.OBJECTS_ENABLED = false;
                player.sendMessage(ChatColor.GREEN + "Les objets sont désactivés.");
                break;
            case 18: // Activer les objets
                Config.OBJECTS_ENABLED = true;
                player.sendMessage(ChatColor.GREEN + "Les objets sont activés.");
                break;

            // Colonne 1 : Force du grappin
            case 1: // Diminuer la force du grappin
                Config.GRAPPLING_FORCE = Math.max(0, Config.GRAPPLING_FORCE - 0.1);
                player.sendMessage(ChatColor.GREEN + "Force du grappin diminuée à " + Config.GRAPPLING_FORCE);
                break;
            case 19: // Augmenter la force du grappin
                Config.GRAPPLING_FORCE += 0.1;
                player.sendMessage(ChatColor.GREEN + "Force du grappin augmentée à " + Config.GRAPPLING_FORCE);
                break;

            // Colonne 2 : Rayon de TP
            case 2: // Diminuer le rayon de TP
                Config.TP_RADIUS = Math.max(0, Config.TP_RADIUS - 0.5);
                player.sendMessage(ChatColor.GREEN + "Rayon de TP diminué à " + Config.TP_RADIUS);
                break;
            case 20: // Augmenter le rayon de TP
                Config.TP_RADIUS += 0.5;
                player.sendMessage(ChatColor.GREEN + "Rayon de TP augmenté à " + Config.TP_RADIUS);
                break;

            // Colonne 3 : Rayon max de la map
            case 3: // Diminuer le rayon max
                Config.MAP_MAX_RADIUS = Math.max(0, Config.MAP_MAX_RADIUS - 1);
                player.sendMessage(ChatColor.GREEN + "Rayon max diminué à " + Config.MAP_MAX_RADIUS);
                break;
            case 21: // Augmenter le rayon max
                Config.MAP_MAX_RADIUS += 1;
                player.sendMessage(ChatColor.GREEN + "Rayon max augmenté à " + Config.MAP_MAX_RADIUS);
                break;

            // Colonne 4 : Multiplicateur KB
            case 4: // Diminuer le multiplicateur KB
                Config.KB_MULTIPLIER = Math.max(0, Config.KB_MULTIPLIER - 1);
                player.sendMessage(ChatColor.GREEN + "Multiplicateur KB diminué à " + Config.KB_MULTIPLIER);
                break;
            case 22: // Augmenter le multiplicateur KB
                Config.KB_MULTIPLIER += 1;
                player.sendMessage(ChatColor.GREEN + "Multiplicateur KB augmenté à " + Config.KB_MULTIPLIER);
                break;

            // Colonne 5 : Base KB
            case 5: // Diminuer la base KB
                Config.KB_BASE = Math.max(0, Config.KB_BASE - 0.1);
                player.sendMessage(ChatColor.GREEN + "Base KB diminuée à " + Config.KB_BASE);
                break;
            case 23: // Augmenter la base KB
                Config.KB_BASE += 0.1;
                player.sendMessage(ChatColor.GREEN + "Base KB augmentée à " + Config.KB_BASE);
                break;

            // Colonne 6 : Jump Boost II
            case 6: // Désactiver Jump Boost II
                Config.JUMP_BOOST_ENABLED = false;
                player.sendMessage(ChatColor.GREEN + "Jump Boost II désactivé.");
                break;
            case 24: // Activer Jump Boost II
                Config.JUMP_BOOST_ENABLED = true;
                player.sendMessage(ChatColor.GREEN + "Jump Boost II activé.");
                break;

            // Colonne 7 : Gain % par coup
            case 7: // Diminuer le gain % par coup
                Config.HIT_PERCENT_GAIN = Math.max(0, Config.HIT_PERCENT_GAIN - 0.1);
                player.sendMessage(ChatColor.GREEN + "Gain % par coup diminué à " + Config.HIT_PERCENT_GAIN);
                break;
            case 25: // Augmenter le gain % par coup
                Config.HIT_PERCENT_GAIN += 0.1;
                player.sendMessage(ChatColor.GREEN + "Gain % par coup augmenté à " + Config.HIT_PERCENT_GAIN);
                break;

            default:
                break;
        }
        // Réouverture du menu avec les valeurs mises à jour
        ConfigMenu menu = new ConfigMenu();
        player.openInventory(menu.getInventory());
    }
}

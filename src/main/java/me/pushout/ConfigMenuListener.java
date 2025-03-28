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
            // Option 5 : Force du grappin
            case 2: // Augmenter la force du grappin
                Config.GRAPPLING_FORCE += 0.1;
                player.sendMessage(ChatColor.GREEN + "Force du grappin augmentée à " + Config.GRAPPLING_FORCE);
                break;
            case 3: // Diminuer la force du grappin
                Config.GRAPPLING_FORCE = Math.max(0, Config.GRAPPLING_FORCE - 0.1);
                player.sendMessage(ChatColor.GREEN + "Force du grappin diminuée à " + Config.GRAPPLING_FORCE);
                break;
            // Option 6 : Rayon de TP
            case 5: // Augmenter le rayon de TP
                Config.TP_RADIUS += 0.5;
                player.sendMessage(ChatColor.GREEN + "Rayon de TP augmenté à " + Config.TP_RADIUS);
                break;
            case 6: // Diminuer le rayon de TP
                Config.TP_RADIUS = Math.max(0, Config.TP_RADIUS - 0.5);
                player.sendMessage(ChatColor.GREEN + "Rayon de TP diminué à " + Config.TP_RADIUS);
                break;
            // Option 7 : Activation des objets
            case 7: // Afficher cette option (déjà affichée)
                break;
            case 8: // Activer les objets
                Config.OBJECTS_ENABLED = true;
                player.sendMessage(ChatColor.GREEN + "Les objets sont activés.");
                break;
            case 0: // Désactiver les objets (on utilise slot 0 pour le bouton "désactiver")
                Config.OBJECTS_ENABLED = false;
                player.sendMessage(ChatColor.GREEN + "Les objets sont désactivés.");
                break;
            // Option 1 : Multiplicateur KB
            case 11: // Augmenter le multiplicateur KB
                Config.KB_MULTIPLIER += 1;
                player.sendMessage(ChatColor.GREEN + "Multiplicateur KB augmenté à " + Config.KB_MULTIPLIER);
                break;
            case 12: // Diminuer le multiplicateur KB
                Config.KB_MULTIPLIER = Math.max(0, Config.KB_MULTIPLIER - 1);
                player.sendMessage(ChatColor.GREEN + "Multiplicateur KB diminué à " + Config.KB_MULTIPLIER);
                break;
            // Option 2 : Base KB
            case 14: // Augmenter la base KB
                Config.KB_BASE += 0.1;
                player.sendMessage(ChatColor.GREEN + "Base KB augmentée à " + Config.KB_BASE);
                break;
            case 15: // Diminuer la base KB
                Config.KB_BASE = Math.max(0, Config.KB_BASE - 0.1);
                player.sendMessage(ChatColor.GREEN + "Base KB diminuée à " + Config.KB_BASE);
                break;
            // Option 3 : Jump Boost II
            case 20: // Activer Jump Boost II
                Config.JUMP_BOOST_ENABLED = true;
                player.sendMessage(ChatColor.GREEN + "Jump Boost II activé.");
                break;
            case 21: // Désactiver Jump Boost II
                Config.JUMP_BOOST_ENABLED = false;
                player.sendMessage(ChatColor.GREEN + "Jump Boost II désactivé.");
                break;
            // Option 4 : Gain % par coup
            case 23: // Augmenter le gain % par coup
                Config.HIT_PERCENT_GAIN += 0.1;
                player.sendMessage(ChatColor.GREEN + "Gain % par coup augmenté à " + Config.HIT_PERCENT_GAIN);
                break;
            case 24: // Diminuer le gain % par coup
                Config.HIT_PERCENT_GAIN = Math.max(0, Config.HIT_PERCENT_GAIN - 0.1);
                player.sendMessage(ChatColor.GREEN + "Gain % par coup diminué à " + Config.HIT_PERCENT_GAIN);
                break;
            // Option 8 : Rayon maximum de la map
            case 9: // Affichage (bouton de l'option, déjà affiché)
                break;
            case 16: // Augmenter le rayon max de la map
                Config.MAP_MAX_RADIUS += 1;
                player.sendMessage(ChatColor.GREEN + "Rayon maximum de la map augmenté à " + Config.MAP_MAX_RADIUS);
                break;
            case 17: // Diminuer le rayon max de la map
                Config.MAP_MAX_RADIUS = Math.max(0, Config.MAP_MAX_RADIUS - 1);
                player.sendMessage(ChatColor.GREEN + "Rayon maximum de la map diminué à " + Config.MAP_MAX_RADIUS);
                break;
            default:
                break;
        }
        // Réouverture du menu avec les valeurs mises à jour
        ConfigMenu menu = new ConfigMenu();
        player.openInventory(menu.getInventory());
    }
}

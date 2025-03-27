package me.pushout;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
            case 11: // Increase KB Multiplier
                Config.KB_MULTIPLIER += 1;
                player.sendMessage(ChatColor.GREEN + "KB Multiplier increased to " + Config.KB_MULTIPLIER);
                break;
            case 12: // Decrease KB Multiplier
                Config.KB_MULTIPLIER = Math.max(0, Config.KB_MULTIPLIER - 1);
                player.sendMessage(ChatColor.GREEN + "KB Multiplier decreased to " + Config.KB_MULTIPLIER);
                break;
            case 14: // Increase KB Base
                Config.KB_BASE += 0.1;
                player.sendMessage(ChatColor.GREEN + "KB Base increased to " + Config.KB_BASE);
                break;
            case 15: // Decrease KB Base
                Config.KB_BASE = Math.max(0, Config.KB_BASE - 0.1);
                player.sendMessage(ChatColor.GREEN + "KB Base decreased to " + Config.KB_BASE);
                break;
            case 20: // Enable Jump Boost II
                Config.JUMP_BOOST_ENABLED = true;
                player.sendMessage(ChatColor.GREEN + "Jump Boost II enabled.");
                break;
            case 21: // Disable Jump Boost II
                Config.JUMP_BOOST_ENABLED = false;
                player.sendMessage(ChatColor.GREEN + "Jump Boost II disabled.");
                break;
            case 23: // Increase Hit % Gain
                Config.HIT_PERCENT_GAIN += 0.1;
                player.sendMessage(ChatColor.GREEN + "Hit % Gain increased to " + Config.HIT_PERCENT_GAIN);
                break;
            case 24: // Decrease Hit % Gain
                Config.HIT_PERCENT_GAIN = Math.max(0, Config.HIT_PERCENT_GAIN - 0.1);
                player.sendMessage(ChatColor.GREEN + "Hit % Gain decreased to " + Config.HIT_PERCENT_GAIN);
                break;
            default:
                break;
        }
        // Réouverture du menu avec les valeurs mises à jour
        ConfigMenu menu = new ConfigMenu();
        player.openInventory(menu.getInventory());
    }
}

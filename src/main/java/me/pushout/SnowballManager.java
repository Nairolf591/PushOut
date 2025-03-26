package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class SnowballManager implements Listener {
    private static final int MAX_SNOWBALLS = 16;
    private static final long COOLDOWN_MS = 7000;
    // Map pour gérer le temps de recharge par joueur
    private static HashMap<UUID, Long> snowballCooldown = new HashMap<>();

    public SnowballManager() {
        // Tâche répétitive pour recharger les boules de neige, mais uniquement si une partie est en cours
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!GameManager.getInstance().isGameRunning()) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int count = player.getInventory().all(Material.SNOWBALL).values().stream()
                            .mapToInt(ItemStack::getAmount).sum();
                    if (count < MAX_SNOWBALLS) {
                        UUID uuid = player.getUniqueId();
                        long now = System.currentTimeMillis();
                        long cooldownEnd = snowballCooldown.getOrDefault(uuid, now);
                        if (now >= cooldownEnd) {
                            // Ajoute une boule de neige et redéfinit le cooldown
                            player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 1));
                            snowballCooldown.put(uuid, now + COOLDOWN_MS);
                        }
                    }
                }
            }
        }.runTaskTimer(PushOut.getInstance(), 20L, 20L);
    }

    // Affichage de l'action bar lorsque le joueur tient une boule de neige
    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item != null && item.getType() == Material.SNOWBALL) {
            UUID uuid = player.getUniqueId();
            int count = player.getInventory().all(Material.SNOWBALL).values().stream()
                    .mapToInt(ItemStack::getAmount).sum();
            if (count < MAX_SNOWBALLS) {
                long now = System.currentTimeMillis();
                long cooldownEnd = snowballCooldown.getOrDefault(uuid, now);
                long remaining = Math.max(cooldownEnd - now, 0);
                // Construction d'une barre de progression similaire à celle du grappin
                double progress = (double)(COOLDOWN_MS - remaining) / COOLDOWN_MS;
                int totalBars = 10;
                int filledBars = (int) (progress * totalBars);
                StringBuilder bar = new StringBuilder();
                for (int i = 0; i < filledBars; i++) {
                    bar.append("█");
                }
                for (int i = 0; i < totalBars - filledBars; i++) {
                    bar.append("░");
                }
                player.sendActionBar("§eBoules de neige: " + count + " §7| Recharge: " + bar.toString());
            }
        }
    }
}

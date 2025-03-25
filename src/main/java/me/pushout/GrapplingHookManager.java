package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class GrapplingHookManager implements Listener {
    private final HashMap<UUID, Integer> usesLeft = new HashMap<>();
    private final HashMap<UUID, Long> lastUse = new HashMap<>();
    private static final int MAX_USES = 5;
    private static final int COOLDOWN = 7; // en secondes

    public GrapplingHookManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : usesLeft.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && usesLeft.get(uuid) < MAX_USES) {
                        long timeSinceLast = (System.currentTimeMillis() - lastUse.getOrDefault(uuid, 0L)) / 1000;
                        if (timeSinceLast >= COOLDOWN) {
                            usesLeft.put(uuid, usesLeft.get(uuid) + 1);
                            lastUse.put(uuid, System.currentTimeMillis());
                        }
                        updateActionBar(player);
                    }
                }
            }
        }.runTaskTimer(PushOut.getInstance(), 20L, 20L);
    }

    public static ItemStack createGrapplingHook() {
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = fishingRod.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Grappin");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            fishingRod.setItemMeta(meta);
        }
        return fishingRod;
    }

    @EventHandler
    public void onGrappleUse(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT) {
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
                UUID uuid = player.getUniqueId();
                usesLeft.putIfAbsent(uuid, MAX_USES);
                
                if (usesLeft.get(uuid) > 0) {
                    usesLeft.put(uuid, usesLeft.get(uuid) - 1);
                    lastUse.put(uuid, System.currentTimeMillis());
                    propelPlayer(player, event);
                } else {
                    player.sendMessage("§cGrappin en recharge !");
                }
                updateActionBar(player);
            }
        }
    }

    private void propelPlayer(Player player, PlayerFishEvent event) {
        Vector direction = player.getLocation().getDirection().normalize();
        double distance = event.getHook().getLocation().distance(player.getLocation());
        double force = Math.min(distance * 0.5, 2.0) + 1; // Force similaire à l'ancienne méthode
        Vector velocity = direction.multiply(force);
        player.setVelocity(velocity);
    }

    private void updateActionBar(Player player) {
        int remainingUses = usesLeft.getOrDefault(player.getUniqueId(), MAX_USES);
        long nextRecharge = COOLDOWN - ((System.currentTimeMillis() - lastUse.getOrDefault(player.getUniqueId(), 0L)) / 1000);
        nextRecharge = Math.max(nextRecharge, 0);

        // Construction d'une barre de progression
        double progress = (COOLDOWN - nextRecharge) / (double) COOLDOWN;
        int totalBars = 10;
        int filledBars = (int) (progress * totalBars);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filledBars; i++) {
            bar.append("█");
        }
        for (int i = 0; i < totalBars - filledBars; i++) {
            bar.append("░");
        }

        player.sendActionBar("§eGrappins: §a" + remainingUses + " §7| Recharge: " + bar.toString());
    }
}

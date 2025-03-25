package me.pushout;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class KOManager {
    // Passage en static pour partager l'état entre tous les appels (y compris lors des events)
    private static HashMap<UUID, Integer> koPercent = new HashMap<>();

    public void addKO(Player player, int amount) {
        UUID playerId = player.getUniqueId();
        int newPercent = koPercent.getOrDefault(playerId, 0) + amount;
        koPercent.put(playerId, newPercent);
        player.setExp(newPercent / 100.0f);
        player.setLevel(newPercent);
        applyKnockback(player, newPercent);
    }

    private void applyKnockback(Player player, int percent) {
        double knockbackMultiplier = 0.5 + (percent / 100.0);
        Vector knockback = player.getLocation().getDirection().multiply(knockbackMultiplier);
        player.setVelocity(knockback);
    }

    // Rendre la méthode statique pour pouvoir la réinitialiser depuis GameManager
    public static void resetKO(Player player) {
        koPercent.put(player.getUniqueId(), 0);
        player.setExp(0);
        player.setLevel(0);
    }

    public int getKO(Player player) {
        return koPercent.getOrDefault(player.getUniqueId(), 0);
    }
}

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
        // Mise à jour de la barre d'XP
        player.setExp(Math.min(newPercent / 100.0f, 1.0f));
        player.setLevel(newPercent);
        applyKnockback(player, newPercent);
        // Mise à jour du Scoreboard au-dessus de la tête
        updatePlayerScoreboard(player);
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
        updatePlayerScoreboard(player);
    }

    public int getKO(Player player) {
        return koPercent.getOrDefault(player.getUniqueId(), 0);
    }

     public static void updatePlayerScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        Objective obj = board.getObjective("koPercentage");
        if (obj == null) {
            // Crée un nouvel objectif pour afficher le KO % en dessous du nom du joueur
            obj = board.registerNewObjective("koPercentage", "dummy", "KO %");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        int percent = koPercent.getOrDefault(player.getUniqueId(), 0);
        Score score = obj.getScore(player.getName());
        score.setScore(percent);
    }
}

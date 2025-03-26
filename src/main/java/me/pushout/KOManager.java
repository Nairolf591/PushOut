package me.pushout;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class KOManager {
// Utilisation de double pour gérer les pourcentages avec décimales
    private static HashMap<UUID, Double> koPercent = new HashMap<>();

    public void addKO(Player player, double amount) {
        UUID playerId = player.getUniqueId();
        double newPercent = koPercent.getOrDefault(playerId, 0.0) + amount;
        koPercent.put(playerId, newPercent);
        // Mise à jour de la barre XP :
        // La barre représente le reste modulo 100, pour une barre toujours pleine à 100%
        player.setExp((float)((newPercent % 100) / 100.0));
        player.setLevel((int)newPercent); // Affiche le pourcentage entier
        applyKnockback(player, newPercent);
        updatePlayerScoreboard(player);
    }

    private void applyKnockback(Player player, double percent) {
        double knockbackMultiplier = 0.5 + (percent / 100.0);
        Vector knockback = player.getLocation().getDirection().multiply(knockbackMultiplier);
        player.setVelocity(knockback);
    }

    // Rendre la méthode statique pour pouvoir la réinitialiser depuis GameManager
    public static void resetKO(Player player) {
        koPercent.put(player.getUniqueId(), 0.0);
        player.setExp(0);
        player.setLevel(0);
        updatePlayerScoreboard(player);
    }

    public double getKO(Player player) {
        return koPercent.getOrDefault(player.getUniqueId(), 0.0);
    }
    // Met à jour le Scoreboard affiché au-dessus du pseudo avec une couleur en fonction du pourcentage
    public static void updatePlayerScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        Objective obj = board.getObjective("koPercentage");
        if (obj == null) {
            obj = board.registerNewObjective("koPercentage", "dummy", "KO %");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        double percent = koPercent.getOrDefault(player.getUniqueId(), 0.0);
        String color;
        if (percent < 80)
            color = ChatColor.GREEN.toString();
        else if (percent < 130)
            color = ChatColor.GOLD.toString(); // orange
        else if (percent < 180)
            color = ChatColor.RED.toString();
        else if (percent < 250)
            color = ChatColor.DARK_RED.toString();
        else
            color = ChatColor.BLACK.toString();
        // On affiche le pourcentage avec sa couleur (le Scoreboard affichera le score numérique)
        Score score = obj.getScore(color + player.getName());
        score.setScore((int) percent);
    }
}

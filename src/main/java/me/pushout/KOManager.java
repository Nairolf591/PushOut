package me.pushout;

import org.bukkit.Bukkit;
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

    public void addKO(Player player, double amount, Vector hitDirection) {
        UUID playerId = player.getUniqueId();
        double newPercent = koPercent.getOrDefault(playerId, 0.0) + amount;
        koPercent.put(playerId, newPercent);
        // Mise à jour de la barre XP (modulo 100) et du niveau (pour le total)
        player.setExp((float) ((newPercent % 100) / 100.0));
        player.setLevel((int) newPercent);
        // Appliquer le KB en utilisant le vecteur passé en paramètre
        double knockbackMultiplier = (0.5 + (newPercent / 100.0)) * 2.5; // multiplication par 3 au lieu de 4
        Vector knockback = hitDirection.normalize().multiply(knockbackMultiplier);
        player.setVelocity(knockback);
        updatePlayerScoreboard(player);
    }


    public void addKO(Player player, double amount) {
        UUID playerId = player.getUniqueId();
        double newPercent = koPercent.getOrDefault(playerId, 0.0) + amount;
        koPercent.put(playerId, newPercent);
        // Mise à jour de la barre XP (modulo 100) et du niveau (pour le total)
        player.setExp((float) ((newPercent % 100) / 100.0));
        player.setLevel((int) newPercent);
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

    public static void updatePlayerScoreboard(Player updatedPlayer) {
        // Mise à jour de l'affichage "below name" pour le joueur lui-même
        Scoreboard board = updatedPlayer.getScoreboard();
        Objective below = board.getObjective("koPercentage");
        if (below == null) {
            below = board.registerNewObjective("koPercentage", "dummy", "KO %");
            below.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        double percent = koPercent.getOrDefault(updatedPlayer.getUniqueId(), 0.0);
        String color;
        if (percent < 80)
            color = ChatColor.GREEN.toString();
        else if (percent < 130)
            color = ChatColor.GOLD.toString();
        else if (percent < 180)
            color = ChatColor.RED.toString();
        else if (percent < 250)
            color = ChatColor.DARK_RED.toString();
        else
            color = ChatColor.BLACK.toString();
        Score scoreBelow = below.getScore(color + updatedPlayer.getName());
        scoreBelow.setScore((int) percent);

        // Mise à jour du scoreboard global en sidebar pour TOUS les joueurs
        for (Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard pb = p.getScoreboard();
            Objective sidebar = pb.getObjective("koSidebar");
            if (sidebar == null) {
                sidebar = pb.registerNewObjective("koSidebar", "dummy", "KO %");
                sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            // Pour chaque joueur, on met à jour sa ligne
            for (Player target : Bukkit.getOnlinePlayers()) {
                int targetPercent = koPercent.getOrDefault(target.getUniqueId(), 0.0).intValue();
                sidebar.getScore(target.getName()).setScore(targetPercent);
            }
        }
    }
}

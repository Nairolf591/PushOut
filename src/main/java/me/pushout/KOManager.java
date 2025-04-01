package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
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
        player.setExp((float)((newPercent % 100) / 100.0));
        player.setLevel((int)newPercent);
        // Augmentation du multiplicateur pour accentuer l'impact du KO
        double knockbackMultiplier = 0.5 + Math.pow(newPercent / 100.0, 2) * 16.0;
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
        Scoreboard board = updatedPlayer.getScoreboard();

        // Récupère ou crée une team avec le nom du joueur
        Team team = board.getTeam(updatedPlayer.getName());
        if (team == null) {
            team = board.registerNewTeam(updatedPlayer.getName());
            team.addEntry(updatedPlayer.getName());
        }

        // Récupère le KO % actuel
        double percent = koPercent.getOrDefault(updatedPlayer.getUniqueId(), 0.0);

        // Formate le pourcentage avec le signe %
        String formattedPercent = String.format("%.0f%%", percent);

        // Applique la couleur selon le pourcentage
        String coloredPercent;
        if (percent < 50) {
            coloredPercent = ChatColor.GREEN + formattedPercent;
        } else if (percent < 80) {
            coloredPercent = ChatColor.GOLD + formattedPercent;
        } else {
            coloredPercent = ChatColor.RED + formattedPercent;
        }

        // Met à jour le suffixe de la team pour afficher le pourcentage sous le pseudo
        team.setSuffix(" " + coloredPercent);

        // Mise à jour du scoreboard global en sidebar uniquement si la partie est en cours
        if (GameManager.getInstance().isGameRunning()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Scoreboard pb = p.getScoreboard();
                Objective sidebar = pb.getObjective("koSidebar");
                if (sidebar == null) {
                    sidebar = pb.registerNewObjective("koSidebar", "dummy", "KO %");
                    sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
                }
                for (Player target : Bukkit.getOnlinePlayers()) {
                    int targetPercent = koPercent.getOrDefault(target.getUniqueId(), 0.0).intValue();
                    sidebar.getScore(target.getName()).setScore(targetPercent);
                }
            }
        } else {
            // Si la partie n'est pas en cours, on efface la sidebar de tous
            for (Player p : Bukkit.getOnlinePlayers()) {
                Scoreboard pb = p.getScoreboard();
                pb.clearSlot(DisplaySlot.SIDEBAR);
            }
        }
    }

}

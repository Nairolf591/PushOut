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
        if (!GameManager.getInstance().isGameRunning()) return; // n'applique le KB que pendant la partie
        UUID playerId = player.getUniqueId();
        double newPercent = koPercent.getOrDefault(playerId, 0.0) + amount;
        koPercent.put(playerId, newPercent);
        player.setExp((float)((newPercent % 100) / 100.0));
        player.setLevel((int)newPercent);
        // Utilisation des valeurs de Config pour le calcul du KB
        double knockbackMultiplier = Config.KB_BASE + Math.pow(newPercent / 100.0, 2) * Config.KB_MULTIPLIER;
        Vector knockback = hitDirection.normalize().multiply(knockbackMultiplier);
        player.setVelocity(knockback);
        updatePlayerScoreboard(player);
    }

    public void addKO(Player player, double amount) {
        if (!GameManager.getInstance().isGameRunning()) return;
        UUID playerId = player.getUniqueId();
        double newPercent = koPercent.getOrDefault(playerId, 0.0) + amount;
        koPercent.put(playerId, newPercent);
        player.setExp((float)((newPercent % 100) / 100.0));
        player.setLevel((int)newPercent);
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
        // Affichage sous le pseudo (Below Name)
        Objective below = board.getObjective("koPercentage");
        if (below == null) {
            below = board.registerNewObjective("koPercentage", "dummy", "KO %");
            below.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        double percent = koPercent.getOrDefault(updatedPlayer.getUniqueId(), 0.0);
        // Utilisation du nom du joueur sans modification de clé
        String entry = updatedPlayer.getName();
        below.getScore(entry).setScore((int) percent);

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

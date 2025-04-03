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
        // Calcul du multiplicateur pour accentuer l'impact du KO
        double knockbackMultiplier = Config.KB_BASE + Math.pow(newPercent / 100.0, 2) * Config.KB_MULTIPLIER;

        // Nouveau calcul du knockback pour éviter que le joueur se barre dans le ciel
        Vector horizontal = new Vector(hitDirection.getX(), 0, hitDirection.getZ());
        if (horizontal.lengthSquared() == 0) {
            horizontal = new Vector(0, 0, 1);
        }
        horizontal.normalize();
        Vector finalKB = horizontal.multiply(knockbackMultiplier);
        finalKB.setY(0.5 + (knockbackMultiplier * 0.05)); // Ajuste la hauteur verticale ici
        player.setVelocity(finalKB);

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

        // Récupère ou crée une team pour le joueur (on ne modifie plus le suffixe)
        Team team = board.getTeam(updatedPlayer.getName());
        if (team == null) {
            team = board.registerNewTeam(updatedPlayer.getName());
            team.addEntry(updatedPlayer.getName());
        }

        // Récupère le KO % actuel
        double percent = koPercent.getOrDefault(updatedPlayer.getUniqueId(), 0.0);

        // On ne touche plus au suffixe pour ne pas altérer le pseudo
        // team.setSuffix(" " + coloredPercent);

        // Création ou récupération de l'objectif BELOW_NAME pour afficher le KO % sous le pseudo
        Objective belowName = board.getObjective("koBelowName");
        if(belowName == null) {
            belowName = board.registerNewObjective("koBelowName", "dummy", "KO %");
            belowName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
        // Affiche le pourcentage sous le pseudo (attention : uniquement un entier)
        belowName.getScore(updatedPlayer.getName()).setScore((int) percent);

        // Mise à jour du scoreboard global en sidebar
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

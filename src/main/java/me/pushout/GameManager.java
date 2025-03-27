package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private static GameManager instance;
    private boolean gameRunning = false;
    private final List<Player> players = new ArrayList<>();
    private final Location center = new Location(Bukkit.getWorld("world"), 100, 100, 100); // Centre de la map


    public GameManager() {
        instance = this;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void startGame() {
        gameRunning = true;
        players.clear();
        players.addAll(Bukkit.getOnlinePlayers()); // Tous les joueurs participent

        double radius = 8.0; // Rayon du cercle
        double angleStep = 360.0 / players.size(); // Angle entre chaque joueur

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            double angle = Math.toRadians(i * angleStep); // Convertir en radians
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));

            // Trouver un bloc solide sous le joueur
            int y = findSafeY((int) x, (int) z);
            Location spawnLocation = new Location(center.getWorld(), x, y + 1, z);
            player.teleport(spawnLocation);
            player.sendMessage("§aVous avez été placé sur l'arène !");

           // Réinitialise le KO
            KOManager.resetKO(player);
    
            // Attribution du grappin
            player.getInventory().addItem(GrapplingHookManager.createGrapplingHook());

            // On initialise un Scoreboard personnel (pour l'affichage du KO)
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

            // Vérification : si le joueur tombe en dessous de la couche 30, on le repositionne
            Bukkit.getScheduler().runTaskLater(PushOut.getInstance(), () -> {
                if (player.getLocation().getY() < 30) {
                    int safeY = findSafeY((int) x, (int) z);
                    Location newSpawn = new Location(center.getWorld(), x, safeY + 1, z);
                    player.teleport(newSpawn);
                    player.sendMessage("§cVotre spawn a été modifié pour éviter une mort immédiate.");
                }
            }, 20L);
        }

        // Geler les joueurs pendant le compte à rebours
        freezePlayers(true);

        new BukkitRunnable() {
            int countdown = 10;

            @Override
            public void run() {
                if (countdown > 0) {
                    Bukkit.broadcastMessage("§eLa partie commence dans §c" + countdown + "§e secondes !");
                    countdown--;
                } else {
                    Bukkit.broadcastMessage("§aLa partie commence maintenant !");
                    // Débloque les joueurs
                    freezePlayers(false);
                    // Appliquer les effets Speed II et Jump Boost II pour couvrir toute la partie
                    for (Player player : players) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 1, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 5, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 5, false, false));
                    }
                    cancel();
                }
            }
        }.runTaskTimer(PushOut.getInstance(), 0L, 20L);
    }

    private int findSafeY(int x, int z) {
        int y = center.getWorld().getHighestBlockYAt(x, z); // Trouve le bloc solide le plus haut
        return (center.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) ? y : y - 1;
    }

    private void freezePlayers(boolean freeze) {
        for (Player player : players) {
            player.setWalkSpeed(freeze ? 0 : 0.2f); // 0 pour bloquer, 0.2f = vitesse normale
            player.setFlySpeed(freeze ? 0 : 0.1f);
        }
    }

    public void stopGame() {
        gameRunning = false;
        // Pour chaque joueur, retirer les items et téléporter en (0,0) à la surface
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().remove(Material.SNOWBALL);
            player.getInventory().remove(Material.FISHING_ROD);
            // Téléportation à la surface en (0,0)
            int safeY = Bukkit.getWorld("world").getHighestBlockYAt(0, 0);
            Location safeLocation = new Location(Bukkit.getWorld("world"), 0, safeY + 1, 0);
            player.teleport(safeLocation);
        }
        players.clear();

        // Après 1 seconde, remettre les joueurs en mode survie
        Bukkit.getScheduler().runTaskLater(PushOut.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
        }, 20L);
    }

    public void checkGameEnd() {
        if (gameRunning && players.size() == 1) {
            Player winner = players.get(0);
            Bukkit.broadcastMessage("§6" + winner.getName() + " a gagné la partie !");

            // Effet visuel
            winner.sendTitle("§6🏆 VICTOIRE ! 🏆", "§eVous avez gagné la partie !", 10, 60, 10);

            stopGame();
        }
    }

    public void playerEliminated(Player player) {
        players.remove(player);
        player.sendMessage("§cVous avez été éliminé !");
        player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        checkGameEnd();
    }
}

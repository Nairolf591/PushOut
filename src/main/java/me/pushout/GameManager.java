package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

        double radius = 5.0; // Rayon du cercle
        double angleStep = 360.0 / players.size(); // Angle entre chaque joueur

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            double angle = Math.toRadians(i * angleStep); // Convertir en radians
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));

            // Trouver un bloc solide sous eux
            int y = findSafeY((int) x, (int) z);
            Location spawnLocation = new Location(center.getWorld(), x, y + 1, z);
            player.teleport(spawnLocation);
            player.sendMessage("§aVous avez été placé sur l'arène !");
        }

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
                    freezePlayers(false);
                    this.cancel();
                }
            }
        }.runTaskTimer(PushOut.getInstance(), 0L, 20L); // 20 ticks = 1 seconde
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
        players.clear();
    }

    public void checkGameEnd() {
        if (gameRunning && players.size() == 1) {
            Player winner = players.get(0);
            Bukkit.broadcastMessage("§6" + winner.getName() + " a gagné la partie !");
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

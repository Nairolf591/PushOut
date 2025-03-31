package me.pushout;

import me.pushout.Config;
import me.pushout.GameManager;
import me.pushout.PushOut;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;

public class BaseballBatManager implements Listener {

    private final JavaPlugin plugin = PushOut.getInstance();
    private final Random random = new Random();
    // Taux de spawn configurable via la config (à ajouter dans Config : public static double BASEBALL_BAT_SPAWN_CHANCE = 0.15;)
    private final double spawnChance = Config.BASEBALL_BAT_SPAWN_CHANCE;

    public BaseballBatManager() {
        // Spawner la batte de baseball de façon aléatoire dans l'arène
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!GameManager.getInstance().isGameRunning()) return;
                if (random.nextDouble() < spawnChance) {
                    spawnBaseballBat();
                }
            }
        }.runTaskTimer(plugin, 0L, 200L); // toutes les 10 sec (200 ticks)

        // Effet particulaire pour mettre en valeur la batte posée
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world");
                for (Item item : world.getEntitiesByClass(Item.class)) {
                    if (item.getItemStack().getType() == Material.STICK &&
                            item.getItemStack().hasItemMeta() &&
                            "Batte de baseball".equals(item.getItemStack().getItemMeta().getDisplayName())) {
                        item.getWorld().spawnParticle(Particle.CRIT, item.getLocation().add(0, 0.5, 0),
                                10, 0.2, 0.2, 0.2, 0);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    // Méthode pour faire spawn la batte de baseball dans l'arène
    private void spawnBaseballBat() {
        World world = Bukkit.getWorld("world");
        double centerX = 100, centerZ = 100;
        double maxRadius = Config.MAP_MAX_RADIUS;
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * maxRadius;
        double x = centerX + distance * Math.cos(angle);
        double z = centerZ + distance * Math.sin(angle);
        int y = findSafeY(world, (int) x, (int) z);
        Location spawnLoc = new Location(world, x, y + 1, z);
        ItemStack bat = createBaseballBatItem();
        Item dropped = world.dropItem(spawnLoc, bat);
        dropped.setPickupDelay(40);
    }

    // Méthode pour trouver le Y d'un bloc solide
    private int findSafeY(World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z);
        return (world.getBlockAt(x, y, z).getType() != Material.AIR) ? y : y - 1;
    }

    // Méthode utilitaire pour créer l'item batte de baseball
    public static ItemStack createBaseballBatItem() {
        ItemStack bat = new ItemStack(Material.STICK);
        ItemMeta meta = bat.getItemMeta();
        meta.setDisplayName("Batte de baseball");
        meta.setLore(Arrays.asList("Utilisation unique", "Projette TRÈS LOIN"));
        bat.setItemMeta(meta);
        return bat;
    }

    @EventHandler
    public void onPlayerHitWithBat(EntityDamageByEntityEvent event) {
        // Vérifier que le damager et la cible sont des joueurs
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player))
            return;

        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        ItemStack item = damager.getInventory().getItemInMainHand();

        // Vérifier que l'item est bien notre batte de baseball customisée
        if (item == null || item.getType() != Material.STICK)
            return;
        if (!item.hasItemMeta() || item.getItemMeta() == null)
            return;
        if (!"Batte de baseball".equals(item.getItemMeta().getDisplayName()))
            return;

        // On applique un petit dégât et on balance le joueur très loin
        event.setDamage(1.0);
        Vector direction = victim.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
        double knockbackStrength = 65.0;
        victim.setVelocity(direction.multiply(knockbackStrength));

        victim.sendMessage("§cBOUM ! La batte de baseball t'a envoyé loin !");
        damager.sendMessage("§aNice hit !");

        // Consommation de l'item (utilisation unique)
        item.setAmount(item.getAmount() - 1);
    }
}

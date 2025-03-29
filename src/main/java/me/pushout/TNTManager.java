package me.pushout;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class TNTManager implements Listener {

    private final JavaPlugin plugin = PushOut.getInstance();
    private final Random random = new Random();

    public TNTManager() {
        // Spawner des TNT objets aléatoirement toutes les 10 sec (200 ticks)
        new BukkitRunnable(){
            @Override
            public void run() {
                if (!GameManager.getInstance().isGameRunning()) return;
                // 30% de chance à chaque intervalle de spawn
                if(random.nextDouble() < 0.3){
                    spawnTNT();
                }
            }
        }.runTaskTimer(plugin, 0L, 200L);

        // Particules sur les TNT posés pour les rendre visibles (tous les 10 ticks)
        new BukkitRunnable(){
            @Override
            public void run() {
                World world = Bukkit.getWorld("world");
                for (Item item : world.getEntitiesByClass(Item.class)) {
                    if (item.getItemStack().getType() == Material.TNT) {
                        item.getWorld().spawnParticle(Particle.FLAME, item.getLocation().add(0, 0.5, 0), 10, 0.2, 0.2, 0.2, 0);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    // Méthode pour spawner un TNT objet à une position aléatoire dans l'arène
    public void spawnTNT(){
        World world = Bukkit.getWorld("world");
        // On définit le centre (comme dans GameManager : (100, _, 100))
        double centerX = 100, centerZ = 100;
        double maxRadius = Config.MAP_MAX_RADIUS;
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * maxRadius;
        double x = centerX + distance * Math.cos(angle);
        double z = centerZ + distance * Math.sin(angle);
        int y = findSafeY(world, (int) x, (int) z);
        Location spawnLoc = new Location(world, x, y + 1, z);
        ItemStack tntStack = new ItemStack(Material.TNT);
        Item tntItem = world.dropItem(spawnLoc, tntStack);
        tntItem.setPickupDelay(40); // petit délai pour éviter le spam
    }

    // Méthode safeY pour obtenir le Y d'un bloc solide
    private int findSafeY(World world, int x, int z){
        int y = world.getHighestBlockYAt(x, z);
        if(world.getBlockAt(x, y, z).getType() != Material.AIR)
            return y;
        else
            return y - 1;
    }

    // Quand un joueur clique avec une TNT en main, on la lance comme projectile
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (!GameManager.getInstance().isGameRunning()) return;
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand != null && itemInHand.getType() == Material.TNT) {
            // Retirer 1 TNT de l'inventaire
            itemInHand.setAmount(itemInHand.getAmount() - 1);
            // Lancer un projectile de type Snowball avec metadata "tntProjectile"
            Snowball tntProj = player.launchProjectile(Snowball.class);
            tntProj.setVelocity(player.getLocation().getDirection().multiply(1.5));
            tntProj.setMetadata("tntProjectile", new FixedMetadataValue(plugin, true));
            // Effet particle sur le lanceur
            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 1);
        }
    }

    // Quand le projectile TNT touche un joueur ou le sol, on déclenche l'explosion custom
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if (event.getEntity() instanceof Snowball && event.getEntity().hasMetadata("tntProjectile")) {
            Snowball proj = (Snowball) event.getEntity();
            Location impactLoc = proj.getLocation();
            World world = impactLoc.getWorld();

            // Effet d'explosion visuel et sonore sans casser le décor
            world.spawnParticle(Particle.EXPLOSION_LARGE, impactLoc, 10);
            world.playSound(impactLoc, "entity.generic.explode", 1.0f, 1.0f);

            // Parcourir tous les joueurs pour appliquer l'effet en fonction de la distance
            for (Player p : world.getPlayers()){
                double dist = p.getLocation().distance(impactLoc);
                if(dist <= 5){
                    double bonusKO;
                    double knockbackStrength;
                    if(dist <= 1){
                        bonusKO = 20.0;
                        knockbackStrength = 14.0;
                    } else {
                        // Interpolation linéaire entre 1 et 5 blocs
                        bonusKO = 20.0 * (5 - dist) / 4;
                        knockbackStrength = 8.0 * (5 - dist) / 4;
                    }
                    Vector direction = p.getLocation().toVector().subtract(impactLoc.toVector()).normalize();
                    new KOManager().addKO(p, bonusKO, direction);
                    p.setVelocity(direction.multiply(knockbackStrength));
                }
            }
            proj.remove();
        }
    }
}

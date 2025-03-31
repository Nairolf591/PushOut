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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;

public class StickyGrenadeManager implements Listener {

    private final JavaPlugin plugin = PushOut.getInstance();
    private final Random random = new Random();
    // La chance de spawn de la grenade collante (modifiable via Config)
    private final double spawnChance = Config.STICKY_GRENADE_SPAWN_CHANCE;

    public StickyGrenadeManager() {
        // Spawner la grenade collante de façon aléatoire dans l'arène
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!GameManager.getInstance().isGameRunning()) return;
                if (random.nextDouble() < spawnChance) {
                    spawnStickyGrenade();
                }
            }
        }.runTaskTimer(plugin, 0L, 200L); // toutes les 10 sec (200 ticks)

        // Effet particulaire pour mettre en valeur les grenades posées
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world");
                for (Item item : world.getEntitiesByClass(Item.class)) {
                    if (item.getItemStack().getType() == Material.SLIME_BALL &&
                            item.getItemStack().hasItemMeta() &&
                            "Grenade Collante".equals(item.getItemStack().getItemMeta().getDisplayName())) {
                        item.getWorld().spawnParticle(Particle.SPELL_INSTANT, item.getLocation().add(0, 0.5, 0),
                                10, 0.2, 0.2, 0.2, 0);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    // Méthode de spawn dans l'arène (similaire à la TNT)
    private void spawnStickyGrenade() {
        World world = Bukkit.getWorld("world");
        double centerX = 100, centerZ = 100;
        double maxRadius = Config.MAP_MAX_RADIUS;
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * maxRadius;
        double x = centerX + distance * Math.cos(angle);
        double z = centerZ + distance * Math.sin(angle);
        int y = findSafeY(world, (int) x, (int) z);
        Location spawnLoc = new Location(world, x, y + 1, z);
        ItemStack grenade = createStickyGrenadeItem();
        Item dropped = world.dropItem(spawnLoc, grenade);
        dropped.setPickupDelay(40);
    }

    // Méthode pour trouver le Y d'un bloc solide
    private int findSafeY(World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z);
        return (world.getBlockAt(x, y, z).getType() != Material.AIR) ? y : y - 1;
    }

    // Création de l'item Grenade Collante
    public static ItemStack createStickyGrenadeItem() {
        ItemStack grenade = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = grenade.getItemMeta();
        meta.setDisplayName("Grenade Collante");
        meta.setLore(Arrays.asList("Attention, elle colle !", "Fuse: 8 secondes"));
        grenade.setItemMeta(meta);
        return grenade;
    }

    // Quand un joueur utilise (clique) avec la grenade collante en main
    @EventHandler
    public void onPlayerUseStickyGrenade(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!GameManager.getInstance().isGameRunning()) return;
        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand != null && inHand.getType() == Material.SLIME_BALL &&
                inHand.hasItemMeta() &&
                "Grenade Collante".equals(inHand.getItemMeta().getDisplayName())) {
            // Retirer une grenade de l'inventaire
            inHand.setAmount(inHand.getAmount() - 1);
            // Lancer le projectile (on utilise Snowball comme base)
            Snowball proj = player.launchProjectile(Snowball.class);
            proj.setVelocity(player.getLocation().getDirection().multiply(1.2));
            proj.setMetadata("stickyGrenade", new FixedMetadataValue(plugin, true));
            // Planifier l'explosion dans 8 secondes (160 ticks)
            scheduleExplosion(proj, null);
            player.getWorld().spawnParticle(Particle.SPELL, player.getLocation(), 5);
        }
    }

    // Planifie l'explosion après 8 secondes, en précisant si le projectile est attaché à un joueur
    private void scheduleExplosion(Snowball proj, Player attachedPlayer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (proj.isDead()) return;
                Location explosionLoc;
                if (attachedPlayer != null && attachedPlayer.isOnline()) {
                    explosionLoc = attachedPlayer.getLocation();
                    // Effet particulaire autour du joueur auquel la grenade est collée
                    attachedPlayer.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, explosionLoc, 20, 1, 1, 1, 0);
                } else {
                    explosionLoc = proj.getLocation();
                }
                explode(explosionLoc);
                proj.remove();
            }
        }.runTaskLater(plugin, 160L); // 8 secondes = 160 ticks
    }

    // Méthode d'explosion (effets visuels, sonores et application de KO/knockback)
    private void explode(Location loc) {
        World world = loc.getWorld();
        world.spawnParticle(Particle.EXPLOSION_HUGE, loc, 10);
        world.playSound(loc, "entity.generic.explode", 1.0f, 1.0f);
        for (Player p : world.getPlayers()) {
            double dist = p.getLocation().distance(loc);
            if (dist <= 5) {
                double bonusKO;
                double knockbackStrength;
                if (dist <= 1) {
                    bonusKO = 20.0;
                    knockbackStrength = 25.0;
                } else {
                    bonusKO = 20.0 * (5 - dist) / 4;
                    knockbackStrength = 20.0 * (5 - dist) / 4;
                }
                Vector direction = p.getLocation().toVector().subtract(loc.toVector()).normalize();
                new KOManager().addKO(p, bonusKO, direction);
                p.setVelocity(direction.multiply(knockbackStrength));
                // Particules autour du joueur touché
                p.getWorld().spawnParticle(Particle.CRIT_MAGIC, p.getLocation(), 15, 0.5, 0.5, 0.5, 0);
            }
        }
    }

    // Lorsqu'un projectile de grenade collante touche quelque chose
    @EventHandler
    public void onStickyGrenadeHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().hasMetadata("stickyGrenade")) {
            Snowball proj = (Snowball) event.getEntity();
            // Si le projectile touche un joueur, on le "colle" à lui et on affiche des particules
            if (event.getHitEntity() instanceof Player) {
                Player hitPlayer = (Player) event.getHitEntity();
                proj.setVelocity(new Vector(0, 0, 0));
                proj.teleport(hitPlayer.getLocation());
                hitPlayer.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, hitPlayer.getLocation(), 20, 0.5, 0.5, 0.5, 0);
                // Replanifier l'explosion en attachant le joueur
                scheduleExplosion(proj, hitPlayer);
            } else {
                // Sinon, si ça touche un bloc, on planifie l'explosion à l'impact
                scheduleExplosion(proj, null);
            }
        }
    }
}

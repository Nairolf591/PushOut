package me.pushout;

import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventsListener implements Listener {

    private final KOManager koManager = new KOManager();
    // Pour gérer le cooldown des boules de neige (clé = uuid du lanceur)
    private static final long SNOWBALL_COOLDOWN_MS = 7000;
    private static final java.util.HashMap<java.util.UUID, Long> snowballCooldown = new java.util.HashMap<>();

      @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        // Coup à la main : chaque coup ajoute 2,3%
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            koManager.addKO(victim, 2.3);
        }
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getEntity();
            if (snowball.getShooter() instanceof Player) {
                Player shooter = (Player) snowball.getShooter();
                // Vérification du cooldown
                long now = System.currentTimeMillis();
                if (now < snowballCooldown.getOrDefault(shooter.getUniqueId(), 0L)) {
                    shooter.sendMessage("§cBoule de neige en recharge !");
                    return;
                }
                snowballCooldown.put(shooter.getUniqueId(), now + SNOWBALL_COOLDOWN_MS);
                // Si la boule de neige a touché un joueur
                if (event.getHitEntity() instanceof Player) {
                    Player victim = (Player) event.getHitEntity();
                    // Calcul de la distance entre le lanceur et la victime
                    double distance = shooter.getLocation().distance(victim.getLocation());
                    double bonus = 0;
                    if (distance >= 12)
                        bonus = 5;
                    else if (distance >= 5)
                        bonus = 2;
                    double total = 2.3 + bonus; // 2,3% de base + bonus selon la distance
                    koManager.addKO(victim, total);
                    // Le knockback est appliqué dans KOManager.addKO (mais vous pouvez en ajouter ici si besoin)
                    // Désactive le grappin du joueur touché pendant 0,8 sec
                    GrapplingHookManager.disableGrappling(victim, 800);
                }
            }
        }
    }
}

package me.pushout;

import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class EventsListener implements Listener {

    private final KOManager koManager = new KOManager();
    // Pour gérer le cooldown des boules de neige (clé = uuid du lanceur)
    private static final long SNOWBALL_COOLDOWN_MS = 7000;
    private static final java.util.HashMap<java.util.UUID, Long> snowballCooldown = new java.util.HashMap<>();

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        // Coup à la main : chaque coup ajoute 2,3%
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            // Calcul du vecteur allant de l'attaquant à la victime (pour que le KB projette la victime à l'opposé du coup)
            Vector direction = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize();
            new KOManager().addKO(victim, 2.3, direction);
            // Désactive le grappin du joueur touché pendant 0,8 sec
            GrapplingHookManager.disableGrappling(victim, 800);
        }
    }


    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball) {
            Snowball snowball = (Snowball) event.getEntity();
            if (snowball.getShooter() instanceof Player) {
                Player shooter = (Player) snowball.getShooter();
                // Suppression de la vérification du cooldown pour les effets des boules de neige

                // Si la boule de neige a touché un joueur
                if (event.getHitEntity() instanceof Player) {
                    Player victim = (Player) event.getHitEntity();
                    double distance = shooter.getLocation().distance(victim.getLocation());
                    double bonus = 0;
                    if (distance >= 10)
                        bonus = 5;
                    else if (distance >= 4)
                        bonus = 2;
                    double total = 2.3 + bonus;
                    Vector direction = victim.getLocation().toVector().subtract(shooter.getLocation().toVector()).normalize();
                    new KOManager().addKO(victim, total, direction);
                    GrapplingHookManager.disableGrappling(victim, 800);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Vérifie si la partie est en cours
        if (!GameManager.getInstance().isGameRunning()) return;

        // Si le joueur descend sous Y=0, il est éliminé
        else if ((player.getLocation().getY() < 0) &&(GameManager.getInstance().isGameRunning())) {
            GameManager.getInstance().playerEliminated(player);
        }
    }
}

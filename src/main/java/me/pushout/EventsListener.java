package me.pushout;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventsListener implements Listener {

    private final KOManager koManager = new KOManager();

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            koManager.addKO(victim, 2); // Ajoute 2% de KO
        }
    }

    @EventHandler
    public void onPlayerFallOut(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() < 0) { // Si le joueur tombe hors de la map
            GameManager.getInstance().playerEliminated(player);
        }
    }
}

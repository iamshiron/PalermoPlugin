package at.zobiii.palermo.listeners;

import at.zobiii.palermo.Palermo;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final Palermo plugin;

    public DeathListener(Palermo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.02);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.7f);
        plugin.getTabListService().updatePlayer(player);
    }
}

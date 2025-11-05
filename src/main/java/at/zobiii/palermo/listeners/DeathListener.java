package at.zobiii.palermo.listeners;

import at.zobiii.palermo.Palermo;
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
        plugin.getTabListService().updatePlayer(player);
    }
}

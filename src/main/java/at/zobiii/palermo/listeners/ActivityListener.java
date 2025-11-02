package at.zobiii.palermo.listeners;

import at.zobiii.palermo.afk.AfkManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ActivityListener implements Listener {

    private final AfkManager afkManager;

    public ActivityListener(AfkManager afkManager) {
        this.afkManager = afkManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (hasMoved(event)) {
            registerActivity(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        registerActivity(event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        registerActivity(event.getPlayer());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        registerActivity(event.getPlayer());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        registerActivity(event.getPlayer());
    }
    
    private void registerActivity(Player player) {
        afkManager.registerActivity(player);
    }

    private boolean hasMoved(PlayerMoveEvent event) {
        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            return true;
        }
        return event.getFrom().distanceSquared(event.getTo()) > 0.01;
    }
}
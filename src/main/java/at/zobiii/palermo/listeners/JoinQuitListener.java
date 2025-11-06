package at.zobiii.palermo.listeners;

import at.zobiii.palermo.Palermo;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener{

    private Palermo plugin;

    public JoinQuitListener(Palermo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = ChatColor.BLUE + "+" + ChatColor.GRAY + " " + event.getPlayer().getName();
        event.setJoinMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = ChatColor.RED + "-" + ChatColor.GRAY + " " + event.getPlayer().getName();
        event.setQuitMessage(message);
        
        if (plugin != null) {
            plugin.getAboveNameService().removePlayer(event.getPlayer());
        }
    }
}

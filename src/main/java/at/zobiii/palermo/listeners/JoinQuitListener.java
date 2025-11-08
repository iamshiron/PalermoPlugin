package at.zobiii.palermo.listeners;

import at.zobiii.palermo.Palermo;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final Palermo plugin;

    public JoinQuitListener(Palermo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        String message = ChatColor.BLUE + "+" + ChatColor.GRAY + " " + player.getName();
        event.setJoinMessage(message);
        
        plugin.getAfkManager().registerActivity(player);
        plugin.getScoreboardService().createScoreboard(player);
        plugin.getTabListService().updatePlayer(player);
        plugin.getTabListService().setHeaderFooter(player);
        plugin.getAboveNameService().setupPlayer(player);
        
        showWelcomeTitle(player);
        playWelcomeSound(player);
    }
    
    private void showWelcomeTitle(Player player) {
        String title = ChatColor.GOLD + "" + ChatColor.BOLD + "Welcome";
        String subtitle = ChatColor.GRAY + player.getName();
        player.sendTitle(title, subtitle, 10, 60, 20);
    }
    
    private void playWelcomeSound(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_5, 0.5f, 1.8f);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        String message = ChatColor.RED + "-" + ChatColor.GRAY + " " + player.getName();
        event.setQuitMessage(message);
        
        plugin.getAboveNameService().removePlayer(player);
    }
}

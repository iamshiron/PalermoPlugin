package at.zobiii.palermo.listeners;

import at.zobiii.palermo.Palermo;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final Palermo plugin;

    public PlayerJoinListener(Palermo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
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
        player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 0.5f, 1.2f);
    }
}

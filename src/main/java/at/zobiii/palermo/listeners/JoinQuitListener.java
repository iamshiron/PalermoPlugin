package at.zobiii.palermo.listeners;

import at.zobiii.palermo.Palermo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
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
        spawnJoinParticles(player);
    }
    
    private void showWelcomeTitle(Player player) {
        String title = ChatColor.GOLD + "" + ChatColor.BOLD + "Welcome";
        String subtitle = ChatColor.GRAY + player.getName();
        player.sendTitle(title, subtitle, 10, 60, 20);
    }
    
    private void playWelcomeSound(Player player) {
        player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_5, 0.5f, 1.8f);
    }
    
    private void spawnJoinParticles(Player player) {
        Location loc = player.getLocation();
        for (int i = 0; i < 12; i++) {
            double angle = (Math.PI * 2 * i) / 12;
            double x = Math.cos(angle) * 0.5;
            double z = Math.sin(angle) * 0.5;
            loc.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(x, 1, z), 1, 0, 0.3, 0, 0.02);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        String message = ChatColor.RED + "-" + ChatColor.GRAY + " " + player.getName();
        event.setQuitMessage(message);
        
        plugin.getAboveNameService().removePlayer(player);
    }
}

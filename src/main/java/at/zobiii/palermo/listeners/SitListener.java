package at.zobiii.palermo.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SitListener implements Listener {

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        Entity vehicle = event.getDismounted();

        if(vehicle instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand) vehicle;
            if(!stand.isVisible() && stand.isMarker()){
                if(event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.2f, 1.8f);
                }
                stand.remove();
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (event.isSneaking() && player.isInsideVehicle()) {
            Entity vehicle = player.getVehicle();
            if(vehicle instanceof ArmorStand) {
                player.leaveVehicle();
            }
        }
    }
}

package at.zobiii.palermo.listeners;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class TrashListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        String title = event.getView().getTitle();

        if(title.startsWith("§cTrash Can")){
            if(event.getPlayer() instanceof Player) {
                Player player = (Player) event.getPlayer();
                player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.03);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.4f, 0.5f);
                player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 0.3f, 1.0f);
            }
            event.getInventory().clear();
        }
    }
}

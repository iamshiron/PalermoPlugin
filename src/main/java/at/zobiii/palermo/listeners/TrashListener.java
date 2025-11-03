package at.zobiii.palermo.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class TrashListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        String title = event.getView().getTitle();

        if(title.startsWith("§cTrash Can")){
            event.getInventory().clear();
        }
    }
}

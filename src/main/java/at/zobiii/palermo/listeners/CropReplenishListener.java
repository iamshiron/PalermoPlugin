package at.zobiii.palermo.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CropReplenishListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (!isFullyGrown(block)) {
            return;
        }

        Material blockType = block.getType();
        Material itemType = item.getType();

        if (blockType == Material.WHEAT && itemType == Material.WHEAT_SEEDS) {
            replenishCrop(block, player, event);
        } else if (blockType == Material.BEETROOTS && itemType == Material.BEETROOT_SEEDS) {
            replenishCrop(block, player, event);
        } else if (blockType == Material.CARROTS && itemType == Material.CARROT) {
            replenishCrop(block, player, event);
        } else if (blockType == Material.POTATOES && itemType == Material.POTATO) {
            replenishCrop(block, player, event);
        }
    }

    private boolean isFullyGrown(Block block) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Ageable)) {
            return false;
        }

        Ageable ageable = (Ageable) blockData;
        return ageable.getAge() == ageable.getMaximumAge();
    }

    private void replenishCrop(Block block, Player player, PlayerInteractEvent event) {
        event.setCancelled(true);

        Material cropType = block.getType();

        block.breakNaturally(player.getInventory().getItemInMainHand());

        block.setType(cropType);
        Ageable ageable = (Ageable)  block.getBlockData();
        block.setBlockData(ageable);
    }
}
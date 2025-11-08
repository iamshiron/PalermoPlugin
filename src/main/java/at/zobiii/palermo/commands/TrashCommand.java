package at.zobiii.palermo.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class TrashCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        Inventory trash = Bukkit.createInventory(null, 27, "§cTrash Can");
        player.openInventory(trash);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 0.8f);

        return true;
    }
}

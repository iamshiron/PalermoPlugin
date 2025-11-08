package at.zobiii.palermo.commands;

import at.zobiii.palermo.managers.PrefixManager;
import at.zobiii.palermo.util.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PrefixCommand implements CommandExecutor, TabCompleter{

    private final PrefixManager prefixManager;
    private Consumer<Player> onPrefixChange;

    public PrefixCommand(PrefixManager prefixManager){
        this.prefixManager = prefixManager;
    }

    public void setOnPrefixChange(Consumer<Player> callback) {
        this.onPrefixChange = callback;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof  Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /pre <text> <color> | /pre clear | /pre show");
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            prefixManager.removePrefix(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Your prefix has been removed.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.5f);
            notifyChange(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("show")) {
            PrefixManager.PrefixData data = prefixManager.getPrefix(player.getUniqueId());
            if (data == null) {
                player.sendMessage(ChatColor.YELLOW + "You don't have a prefix set.");
            } else {
                ChatColor color = ColorUtil.parseColor(data.getColor());
                String prefix = color + "[" + data.getText() + "]";
                player.sendMessage("Your current prefix: " + prefix);
            }
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /pre <text> <color>");
            return true;
        }

        String text = args[0];
        String colorInput = args[1];

        if (text.length() > 10) {
            player.sendMessage(ChatColor.RED + "Prefix text is too long. Max 10 characters.");
            return true;
        }

        prefixManager.setPrefix(player.getUniqueId(), text, colorInput);
        ChatColor color = ColorUtil.parseColor(colorInput);
        String preview = color + "[" + text + "]";
        player.sendMessage(ChatColor.GREEN + "Prefix set to: " + preview);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.5f);
        notifyChange(player);
        return true;
    }

    private void notifyChange(Player player) {
        if (onPrefixChange != null) {
            onPrefixChange.accept(player);
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("clear", "show"));
            return completions;
        }

        if (args.length == 2) {
            completions.addAll(Arrays.asList("red", "blue", "green", "yellow", "gold", "aqua", "gray", "white", "#FF5733"));
            return completions;
        }

        return completions;
    }
}

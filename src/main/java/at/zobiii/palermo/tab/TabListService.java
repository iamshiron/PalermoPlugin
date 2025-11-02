package at.zobiii.palermo.tab;

import at.zobiii.palermo.afk.AfkManager;
import at.zobiii.palermo.prefix.PrefixManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TabListService {

    private final PrefixManager prefixManager;
    private final AfkManager afkManager;

    public TabListService(PrefixManager prefixManager, AfkManager afkManager) {
        this.prefixManager = prefixManager;
        this.afkManager = afkManager;
    }

    public void updatePlayer(Player player) {
        String displayName = buildDisplayName(player);
        player.setPlayerListName(displayName);
    }

    public void updateAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    public void setHeaderFooter(Player player) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        
        String header = ChatColor.DARK_GRAY + "────────────────────\n" +
                        ChatColor.GOLD + "✦ " + ChatColor.BOLD + "Palermo" + ChatColor.RESET + ChatColor.GOLD + " ✦\n" +
                        ChatColor.DARK_GRAY + "────────────────────";

        String footer = ChatColor.DARK_GRAY + "────────────────────\n" +
                        ChatColor.GRAY + "Players: " + ChatColor.GREEN + onlinePlayers + "/" + maxPlayers + "\n" +
                        ChatColor.DARK_GRAY + "────────────────────";

        try {
            player.getClass().getMethod("setPlayerListHeaderAndFooter", String.class, String.class)
                    .invoke(player, header, footer);
        } catch (Exception e) {
        }
    }

    private String buildDisplayName(Player player) {
        String prefix = buildPrefix(player);
        String playerName = ChatColor.GRAY + player.getName();

        if (prefix.isEmpty()) {
            return playerName;
        }

        return prefix + " " + playerName;
    }

    private String buildPrefix(Player player) {
        if (afkManager.isAfk(player)) {
            return ChatColor.GRAY + "[AFK]";
        }

        PrefixManager.PrefixData prefixData = prefixManager.getPrefix(player.getUniqueId());
        if (prefixData != null) {
            String color = prefixData.getColor();
            ChatColor chatColor = parseColorCode(color);
            return chatColor + "[" + prefixData.getText() + "]";
        }

        return "";
    }

    private ChatColor parseColorCode(String colorName) {
        if (colorName == null) {
            return ChatColor.WHITE;
        }
        
        try {
            return ChatColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ChatColor.WHITE;
        }
    }
}
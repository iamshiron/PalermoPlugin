package at.zobiii.palermo.listeners;

import at.zobiii.palermo.managers.AfkManager;
import at.zobiii.palermo.managers.PrefixManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFormatListener implements Listener {

    private final PrefixManager prefixManager;
    private final AfkManager afkManager;

    public ChatFormatListener(PrefixManager prefixManager, AfkManager afkManager) {
        this.prefixManager = prefixManager;
        this.afkManager = afkManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = buildPrefix(player);
        
        event.setFormat(prefix + ChatColor.GRAY + "%1$s" + ChatColor.RESET + ": %2$s");
    }

    private String buildPrefix(Player player) {
        if (afkManager.isAfk(player)) {
            return ChatColor.GRAY + "[AFK] ";
        }

        PrefixManager.PrefixData prefixData = prefixManager.getPrefix(player.getUniqueId());
        if (prefixData != null) {
            String color = prefixData.getColor();
            ChatColor chatColor = parseColorCode(color);
            return chatColor + "[" + prefixData.getText() + "] ";
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

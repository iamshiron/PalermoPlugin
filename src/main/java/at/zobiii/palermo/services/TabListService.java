package at.zobiii.palermo.services;

import at.zobiii.palermo.managers.AfkManager;
import at.zobiii.palermo.managers.PrefixManager;
import at.zobiii.palermo.util.TpsTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

public class TabListService {

    private final PrefixManager prefixManager;
    private final AfkManager afkManager;
    private final TpsTracker tpsTracker;

    private int animationFrame = 0;

    public TabListService(PrefixManager prefixManager, AfkManager afkManager, TpsTracker tpsTracker) {
        this.prefixManager = prefixManager;
        this.afkManager = afkManager;
        this.tpsTracker = tpsTracker;
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
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getServer().getMaxPlayers();

        double tps = 20.0;
        try {
            tps = Math.min(20.0, Math.max(0.0, tpsTracker.getTps()));
        } catch (Exception ignored) {
        }

        ChatColor tpsColor = tps >= 19.5 ? ChatColor.GREEN : (tps > 17.0 ? ChatColor.YELLOW : ChatColor.RED);

        String title = buildAnimatedTitle();
        String onlineLine = ChatColor.GRAY + "Online: " + ChatColor.WHITE + online + ChatColor.DARK_GRAY + "/" + ChatColor.WHITE + max;
        String tpsLine = ChatColor.GRAY + "TPS: " + tpsColor + String.format(Locale.US, "%.1f", tps);

        String header = "\n" + title + "\n" + onlineLine + "\n";
        String footer = "\n" + tpsLine + "\n";

        player.setPlayerListHeaderFooter(header, footer);
    }

    public void updateAllHeadersFooters() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setHeaderFooter(player);
        }
        animationFrame++;
    }

    private String buildAnimatedTitle() {
        String base = "Palermo";
        int len = base.length();
        int totalCycle = len + 33;
        int currentPos = animationFrame % totalCycle;

        ChatColor baseColor = ChatColor.GOLD;
        ChatColor highlight = ChatColor.AQUA;

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.BOLD);

        if (currentPos < len) {
            for (int i = 0; i < len; i++) {
                if (i == currentPos) {
                    sb.append(highlight).append(base.charAt(i));
                } else {
                    sb.append(baseColor).append(base.charAt(i));
                }
            }
        } else {
            sb.append(baseColor).append(base);
        }

        return sb.toString();
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

package at.zobiii.palermo.services;

import at.zobiii.palermo.managers.AfkManager;
import at.zobiii.palermo.managers.PrefixManager;
import at.zobiii.palermo.util.TpsTracker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TabListService {

    private final PrefixManager prefixManager;
    private final AfkManager afkManager;
    private final TpsTracker tpsTracker;
    private final StatsService statsService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private int animationFrame = 0;

    public TabListService(PrefixManager prefixManager, AfkManager afkManager, TpsTracker tpsTracker, StatsService statsService) {
        this.prefixManager = prefixManager;
        this.afkManager = afkManager;
        this.tpsTracker = tpsTracker;
        this.statsService = statsService;
    }

    public void updatePlayer(Player player) {
        String displayName = buildDisplayName(player);
        player.setPlayerListName(displayName);
    }

    public void updateAllPlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    public void setHeaderFooter(Player player) {
        String header = buildHeader(player);
        String footer = buildFooter(player);

        player.setPlayerListHeaderFooter(header, footer);
    }

    public void updateAllHeadersFooters() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setHeaderFooter(player);
        }
        animationFrame++;
    }

    private String buildHeader(Player player) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append(ChatColor.GOLD).append("______________________________________").append("\n");
        sb.append("\n");
        sb.append(buildAnimatedTitle()).append("\n");
        sb.append("\n");
        sb.append(ChatColor.AQUA).append(">> ").append(ChatColor.WHITE).append("Welcome ").append(ChatColor.YELLOW).append(player.getName()).append(ChatColor.AQUA).append(" <<").append("\n");
        sb.append("\n");

        int online = Bukkit.getOnlinePlayers().size();
        sb.append(ChatColor.GREEN).append("Online Players: ").append(ChatColor.WHITE).append(online).append("\n");
        sb.append("\n");

        return sb.toString();
    }

    private String buildFooter(Player player) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");

        String date = dateFormat.format(new Date());
        sb.append(ChatColor.BLUE).append("Date: ").append(ChatColor.WHITE).append(date).append("\n");

        int ping = player.getPing();
        String pingColor = ping < 50 ? ChatColor.GREEN.toString() : ping < 100 ? ChatColor.YELLOW.toString() : ChatColor.RED.toString();

        double tps = 20.0;
        try {
            tps = Math.min(20.0, Math.max(0.0, tpsTracker.getTps()));
        } catch (Exception ignored) {
        }
        String tpsColor = tps >= 19.5 ? ChatColor.GREEN.toString() : tps > 17.0 ? ChatColor.YELLOW.toString() : ChatColor.RED.toString();
        String tpsFormatted = String.format(Locale.US, "%.1f", tps);

        sb.append(ChatColor.BLUE).append("Ping: ").append(pingColor).append(ping).append("ms")
                .append(ChatColor.DARK_GRAY).append(" | ")
                .append(ChatColor.BLUE).append("TPS: ").append(tpsColor).append(tpsFormatted).append("\n");

        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        sb.append(ChatColor.BLUE).append("Memory: ").append(ChatColor.WHITE).append(usedMemory).append("/").append(maxMemory).append("MB").append("\n");

        sb.append("\n");
        sb.append(ChatColor.GOLD).append("______________________________________").append("\n");

        return sb.toString();
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
        String deaths = statsService.getDeaths(player) + " Deaths";
        String deathCounter = ChatColor.DARK_GRAY + "   [" + ChatColor.GRAY +  deaths + ChatColor.DARK_GRAY + "]";

        if (prefix.isEmpty()) {
            return playerName + deathCounter;
        }

        return prefix + " " + playerName + deathCounter;
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
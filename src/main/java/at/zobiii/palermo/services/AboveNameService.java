package at.zobiii.palermo.services;

import at.zobiii.palermo.managers.AfkManager;
import at.zobiii.palermo.managers.PrefixManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class AboveNameService {

    private static final String OBJECTIVE_NAME = "palermo_health";
    private static final String TEAM_PREFIX = "pal_";

    private final PrefixManager prefixManager;
    private final AfkManager afkManager;

    public AboveNameService(PrefixManager prefixManager, AfkManager afkManager) {
        this.prefixManager = prefixManager;
        this.afkManager = afkManager;
    }

    public void setupPlayer(Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            setupObjectiveFor(online);
            updateTeamFor(online, player);
            updateHealthFor(online, player);
            
            if (online != player) {
                setupObjectiveFor(player);
                updateTeamFor(player, online);
                updateHealthFor(player, online);
            }
        }
    }

    private void setupObjectiveFor(Player viewer) {
        Scoreboard scoreboard = viewer.getScoreboard();
        if (scoreboard == null || scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            return;
        }
        
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(
                    OBJECTIVE_NAME,
                    "dummy",
                    ChatColor.RED + "❤"
            );
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }
    }

    public void updateTeam(Player player) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            updateTeamFor(viewer, player);
        }
    }

    private void updateTeamFor(Player viewer, Player target) {
        Scoreboard scoreboard = viewer.getScoreboard();
        if (scoreboard == null) return;
        
        String teamName = TEAM_PREFIX + target.getName();
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.addEntry(target.getName());
        }

        String prefix = buildPrefix(target);
        team.setPrefix(prefix);
    }

    public void updateHealth(Player player) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            updateHealthFor(viewer, player);
        }
    }

    private void updateHealthFor(Player viewer, Player target) {
        Scoreboard scoreboard = viewer.getScoreboard();
        if (scoreboard == null) return;
        
        int health = (int) Math.ceil(target.getHealth());
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective != null) {
            objective.getScore(target.getName()).setScore(health);
        }
    }

    public void removePlayer(Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online == player) continue;
            
            Scoreboard scoreboard = online.getScoreboard();
            if (scoreboard == null) continue;
            
            String teamName = TEAM_PREFIX + player.getName();
            Team team = scoreboard.getTeam(teamName);
            if (team != null) {
                team.unregister();
            }
            
            scoreboard.resetScores(player.getName());
        }
    }

    private String buildPrefix(Player player) {
        if (afkManager.isAfk(player)) {
            return ChatColor.GRAY + "[AFK] ";
        }

        PrefixManager.PrefixData prefixData = prefixManager.getPrefix(player.getUniqueId());
        if (prefixData != null) {
            String color = prefixData.getColor();
            ChatColor chatColor = parseChatColor(color);
            return chatColor + "[" + prefixData.getText() + "] ";
        }

        return "";
    }

    private ChatColor parseChatColor(String colorName) {
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

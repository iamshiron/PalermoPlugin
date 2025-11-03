package at.zobiii.palermo.services;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreboardService {

    private static final int TOTAL_PAGES = 4;
    private int currentPage = 0;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private Double cachedTps = 20.0;

    public void setTps(double tps) {
        this.cachedTps = tps;
    }

    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("palermo", "dummy",
                ChatColor.GOLD + "" + ChatColor.BOLD + "Palermo");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null || scoreboard.getObjective("palermo") == null) {
            return;
        }

        Objective objective = scoreboard.getObjective("palermo");
        clearScores(scoreboard);

        switch (currentPage) {
            case 0 -> updatePlayersPage(objective);
            case 1 -> updateTpsPage(objective);
            case 2 -> updatePingPage(objective, player);
            case 3 -> updateTimePage(objective);
        }
    }

    public void nextPage() {
        currentPage = (currentPage + 1) % TOTAL_PAGES;
    }

    private void updatePlayersPage(Objective objective) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        
        objective.getScore(" ").setScore(4);
        objective.getScore("§7Players").setScore(3);
        objective.getScore("  §a" + onlinePlayers + "§7/§a" + maxPlayers).setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore("§7» Online Players").setScore(0);
    }

    private void updateTpsPage(Objective objective) {
        double tps = cachedTps;
        String tpsFormatted = String.format("%.1f", Math.min(tps, 20.0));
        String tpsColor = tps >= 19.0 ? "§a" : tps >= 15.0 ? "§e" : "§c";
        
        objective.getScore(" ").setScore(4);
        objective.getScore("§7Server TPS").setScore(3);
        objective.getScore("  " + tpsColor + tpsFormatted).setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore("§7» Performance").setScore(0);
    }

    private void updatePingPage(Objective objective, Player player) {
        int ping = player.getPing();
        String pingColor = ping < 50 ? "§a" : ping < 100 ? "§e" : "§c";
        
        objective.getScore(" ").setScore(4);
        objective.getScore("§7Your Ping").setScore(3);
        objective.getScore("  " + pingColor + ping + " ms").setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore("§7» Connection").setScore(0);
    }

    private void updateTimePage(Objective objective) {
        Date now = new Date();
        String currentTime = timeFormat.format(now);
        String currentDate = dateFormat.format(now);
        
        objective.getScore(" ").setScore(5);
        objective.getScore("§7Date").setScore(4);
        objective.getScore("  §b" + currentDate).setScore(3);
        objective.getScore("§7Time").setScore(2);
        objective.getScore("  §b" + currentTime).setScore(1);
        objective.getScore("§7» Server Time").setScore(0);
    }

    private void clearScores(Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("palermo");
        if (objective != null) {
            for (String entry : scoreboard.getEntries()) {
                scoreboard.resetScores(entry);
            }
        }
    }
}

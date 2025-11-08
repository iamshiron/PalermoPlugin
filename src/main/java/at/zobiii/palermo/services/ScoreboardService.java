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

    private static final int TOTAL_PAGES = 7;
    private int currentPage = 0;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private Double cachedTps = 20.0;
    private final StatsService statsService;

    public ScoreboardService(StatsService statsService) {
        this.statsService = statsService;
    }

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
            case 0 -> updatePlayersPage(objective, player);
            case 1 -> updateTpsPage(objective, player);
            case 2 -> updatePingPage(objective, player);
            case 3 -> updateTimePage(objective, player);
            case 4 -> updateStatsPage(objective, player);
            case 5 -> updateWeatherPage(objective, player);
            case 6 -> updateWorldStatsPage(objective, player);
        }
    }

    public void nextPage() {
        currentPage = (currentPage + 1) % TOTAL_PAGES;
    }

    private void updatePlayersPage(Objective objective, Player player) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        String playtime = statsService.getPlaytime(player);

        objective.getScore("   ").setScore(6);
        objective.getScore("§8Playtime: §7" + playtime).setScore(5);
        objective.getScore(" ").setScore(4);
        objective.getScore("§7Players").setScore(3);
        objective.getScore("  §a" + onlinePlayers + "§7/§a" + maxPlayers).setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore("§7» Online Players").setScore(0);
    }

    private void updateTpsPage(Objective objective, Player player) {
        double tps = cachedTps;
        String tpsFormatted = String.format("%.1f", Math.min(tps, 20.0));
        String tpsColor = tps >= 19.0 ? "§a" : tps >= 15.0 ? "§e" : "§c";
        String playtime = statsService.getPlaytime(player);

        objective.getScore("   ").setScore(6);
        objective.getScore("§8Playtime: §7" + playtime).setScore(5);
        objective.getScore(" ").setScore(4);
        objective.getScore("§7Server TPS").setScore(3);
        objective.getScore("  " + tpsColor + tpsFormatted).setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore("§7» Performance").setScore(0);
    }

    private void updatePingPage(Objective objective, Player player) {
        int ping = player.getPing();
        String pingColor = ping < 50 ? "§a" : ping < 100 ? "§e" : "§c";
        String playtime = statsService.getPlaytime(player);

        objective.getScore("   ").setScore(6);
        objective.getScore("§8Playtime: §7" + playtime).setScore(5);
        objective.getScore(" ").setScore(4);
        objective.getScore("§7Your Ping").setScore(3);
        objective.getScore("  " + pingColor + ping + " ms").setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore("§7» Connection").setScore(0);
    }

    private void updateTimePage(Objective objective, Player player) {
        Date now = new Date();
        String currentTime = timeFormat.format(now);
        String currentDate = dateFormat.format(now);
        String playtime = statsService.getPlaytime(player);

        objective.getScore("     ").setScore(8);
        objective.getScore("§8Playtime: §7" + playtime).setScore(7);
        objective.getScore("    ").setScore(6);
        objective.getScore("§7Date").setScore(5);
        objective.getScore("  §b" + currentDate).setScore(4);
        objective.getScore("§7Time").setScore(3);
        objective.getScore("  §b" + currentTime).setScore(2);
        objective.getScore("   ").setScore(1);
        objective.getScore("§7» Server Time").setScore(0);
    }

    private void updateStatsPage(Objective objective, Player player) {
        try {
            String playtime = statsService.getPlaytime(player);
            int deaths = statsService.getDeaths(player);
            int playerKills = statsService.getPlayerKills(player);
            int mobKills = statsService.getMobKills(player);
            int blocksBroken = statsService.getBlocksBroken(player);
            int distance = statsService.getDistanceWalked(player);
            int jumps = statsService.getJumps(player);
            String timeSinceDeath = statsService.getTimeSinceDeath(player);

            objective.getScore("      ").setScore(12);
            objective.getScore("§8Playtime: §7" + playtime).setScore(11);
            objective.getScore("    ").setScore(10);
            objective.getScore("§7Deaths: §f" + deaths).setScore(9);
            objective.getScore("§7PvP Kills: §f" + playerKills).setScore(8);
            objective.getScore("§7Mob Kills: §f" + mobKills).setScore(7);
            objective.getScore("   ").setScore(6);
            objective.getScore("§7Blocks: §f" + blocksBroken).setScore(5);
            objective.getScore("§7Distance: §f" + distance + "m").setScore(4);
            objective.getScore("§7Jumps: §f" + jumps).setScore(3);
            objective.getScore("§7Since Death: §f" + timeSinceDeath).setScore(2);
            objective.getScore(" ").setScore(1);
            objective.getScore("§7» Personal Stats").setScore(0);
        } catch (Exception e) {
            objective.getScore("§cError loading stats").setScore(1);
            objective.getScore("§7» Personal Stats").setScore(0);
            e.printStackTrace();
        }
    }

    private void updateWeatherPage(Objective objective, Player player) {
        String playtime = statsService.getPlaytime(player);
        org.bukkit.World world = player.getWorld();

        boolean hasStorm = world.hasStorm();
        boolean thundering = world.isThundering();
        int weatherDuration = world.getWeatherDuration();
        int weatherMinutes = weatherDuration / 1200;

        long time = world.getTime();
        String timeOfDay = getTimeOfDay(time);

        String weatherStatus = thundering ? "§c⚡ Thunder" : hasStorm ? "§9☂ Rain" : "§e☀ Clear";

        objective.getScore("         ").setScore(10);
        objective.getScore("§8Playtime: §7" + playtime).setScore(9);
        objective.getScore("        ").setScore(8);
        objective.getScore("§7Weather").setScore(7);
        objective.getScore("  " + weatherStatus).setScore(6);
        objective.getScore("§7Duration").setScore(5);
        objective.getScore("  §b" + weatherMinutes + "m").setScore(4);
        objective.getScore("       ").setScore(3);
        objective.getScore("§7Time: §f" + timeOfDay).setScore(2);
        objective.getScore("      ").setScore(1);
        objective.getScore("§7» Weather Forecast").setScore(0);

    }

    private void updateWorldStatsPage(Objective objective, Player player) {
        String playtime = statsService.getPlaytime(player);
        org.bukkit.World world = player.getWorld();
        
        int entities = world.getEntities().size();
        int chunks = world.getLoadedChunks().length;
        String difficulty = world.getDifficulty().name();
        String biome = player.getLocation().getBlock().getBiome().name();
        biome = biome.replace("_", " ").toLowerCase();
        biome = Character.toUpperCase(biome.charAt(0)) + biome.substring(1);
        
        objective.getScore("       ").setScore(11);
        objective.getScore("§8Playtime: §7" + playtime).setScore(10);
        objective.getScore("     ").setScore(9);
        objective.getScore("§7Biome: §f" + biome).setScore(8);
        objective.getScore("    ").setScore(7);
        objective.getScore("§7Entities: §f" + entities).setScore(6);
        objective.getScore("§7Chunks: §f" + chunks).setScore(5);
        objective.getScore("   ").setScore(4);
        objective.getScore("§7Difficulty").setScore(3);
        objective.getScore("  §6" + difficulty).setScore(2);
        objective.getScore("  ").setScore(1);
        objective.getScore("§7» World Stats").setScore(0);
    }

    private String getTimeOfDay(long time) {
        time = time % 24000;
        if (time < 6000) return "Morning";
        if (time < 12000) return "Day";
        if (time < 18000) return "Evening";
        return "Night";
    }

    private void clearScores(Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("palermo");
        if (objective == null) return;
        
        for (String entry : scoreboard.getEntries()) {
            if (objective.getScore(entry).isScoreSet()) {
                scoreboard.resetScores(entry);
            }
        }
    }
}
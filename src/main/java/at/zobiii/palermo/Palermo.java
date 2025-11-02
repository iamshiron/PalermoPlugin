package at.zobiii.palermo;

import at.zobiii.palermo.afk.AfkManager;
import at.zobiii.palermo.chat.ChatFormatListener;
import at.zobiii.palermo.listeners.ActivityListener;
import at.zobiii.palermo.listeners.JoinQuitListener;
import at.zobiii.palermo.listeners.PlayerJoinListener;
import at.zobiii.palermo.prefix.PrefixCommand;
import at.zobiii.palermo.prefix.PrefixManager;
import at.zobiii.palermo.scoreboard.ScoreboardService;
import at.zobiii.palermo.tab.TabListService;
import at.zobiii.palermo.util.TpsTracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Palermo extends JavaPlugin {

    private PrefixManager prefixManager;
    private AfkManager afkManager;
    private TabListService tabListService;
    private ScoreboardService scoreboardService;
    private TpsTracker tpsTracker;

    private BukkitTask afkCheckTask;
    private BukkitTask scoreboardUpdateTask;
    private BukkitTask scoreboardRotateTask;

    @Override
    public void onEnable() {
        prefixManager = new PrefixManager(this);
        afkManager = new AfkManager();
        tabListService = new TabListService(prefixManager, afkManager);
        scoreboardService = new ScoreboardService();
        tpsTracker = new TpsTracker();

        afkManager.setAfkChangeCallback(this::onAfkChange);
        tpsTracker.start(this);

        PrefixCommand prefixCommand = new PrefixCommand(prefixManager);
        prefixCommand.setOnPrefixChange(tabListService::updatePlayer);
        getCommand("pre").setExecutor(prefixCommand);
        getCommand("pre").setTabCompleter(prefixCommand);

        getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);
        getServer().getPluginManager().registerEvents(new ActivityListener(afkManager), this);
        getServer().getPluginManager().registerEvents(new ChatFormatListener(prefixManager, afkManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        startSchedulers();

        for (Player player : Bukkit.getOnlinePlayers()) {
            afkManager.registerActivity(player);
            scoreboardService.createScoreboard(player);
            tabListService.updatePlayer(player);
            tabListService.setHeaderFooter(player);
        }

        getLogger().info("Palermo enabled!");
    }

    @Override
    public void onDisable() {
        if (afkCheckTask != null) {
            afkCheckTask.cancel();
        }
        if (scoreboardUpdateTask != null) {
            scoreboardUpdateTask.cancel();
        }
        if (scoreboardRotateTask != null) {
            scoreboardRotateTask.cancel();
        }

        prefixManager.save();

        getLogger().info("Palermo disabled!");
    }

    private void startSchedulers() {
        afkCheckTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                afkManager.checkAfkStatus(player);
            }
        }, 200L, 200L);

        scoreboardUpdateTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            scoreboardService.setTps(tpsTracker.getTps());
            for (Player player : Bukkit.getOnlinePlayers()) {
                scoreboardService.updateScoreboard(player);
            }
        }, 20L, 20L);

        scoreboardRotateTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            scoreboardService.nextPage();
        }, 400L, 400L);
    }

    private void onAfkChange(Player player) {
        tabListService.updatePlayer(player);
    }

    public ScoreboardService getScoreboardService() {
        return scoreboardService;
    }

    public TabListService getTabListService() {
        return tabListService;
    }

    public AfkManager getAfkManager() {
        return afkManager;
    }
}

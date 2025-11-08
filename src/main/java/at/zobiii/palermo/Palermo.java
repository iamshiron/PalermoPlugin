package at.zobiii.palermo;

import at.zobiii.palermo.commands.PrefixCommand;
import at.zobiii.palermo.commands.SitCommand;
import at.zobiii.palermo.commands.TrashCommand;
import at.zobiii.palermo.listeners.AboveNameListener;
import at.zobiii.palermo.listeners.ActivityListener;
import at.zobiii.palermo.listeners.ChatFormatListener;
import at.zobiii.palermo.listeners.CropReplenishListener;
import at.zobiii.palermo.listeners.DeathListener;
import at.zobiii.palermo.listeners.JoinQuitListener;
import at.zobiii.palermo.listeners.SitListener;
import at.zobiii.palermo.listeners.TrashListener;
import at.zobiii.palermo.managers.AfkManager;
import at.zobiii.palermo.managers.PrefixManager;
import at.zobiii.palermo.services.AboveNameService;
import at.zobiii.palermo.services.ScoreboardService;
import at.zobiii.palermo.services.StatsService;
import at.zobiii.palermo.services.TabListService;
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
    private AboveNameService aboveNameService;
    private StatsService statsService;
    private TpsTracker tpsTracker;

    private BukkitTask afkCheckTask;
    private BukkitTask scoreboardUpdateTask;
    private BukkitTask scoreboardRotateTask;
    private BukkitTask tabListUpdateTask;

    @Override
    public void onEnable() {
        prefixManager = new PrefixManager(this);
        afkManager = new AfkManager();
        tpsTracker = new TpsTracker();
        statsService = new StatsService();
        tabListService = new TabListService(prefixManager, afkManager, tpsTracker, statsService);
        scoreboardService = new ScoreboardService(statsService);
        aboveNameService = new AboveNameService(prefixManager, afkManager);

        afkManager.setAfkChangeCallback(this::onAfkChange);
        tpsTracker.start(this);

        PrefixCommand prefixCommand = new PrefixCommand(prefixManager);
        prefixCommand.setOnPrefixChange(player -> {
            tabListService.updatePlayer(player);
            aboveNameService.updateTeam(player);
        });
        getCommand("pre").setExecutor(prefixCommand);
        getCommand("pre").setTabCompleter(prefixCommand);
        getCommand("sit").setExecutor(new SitCommand());
        getCommand("trash").setExecutor(new TrashCommand());

        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new ActivityListener(afkManager), this);
        getServer().getPluginManager().registerEvents(new ChatFormatListener(prefixManager, afkManager), this);
        getServer().getPluginManager().registerEvents(new CropReplenishListener(), this);
        getServer().getPluginManager().registerEvents(new SitListener(), this);
        getServer().getPluginManager().registerEvents(new TrashListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new AboveNameListener(this), this);

        startSchedulers();

        for (Player player : Bukkit.getOnlinePlayers()) {
            afkManager.registerActivity(player);
            scoreboardService.createScoreboard(player);
            tabListService.updatePlayer(player);
            tabListService.setHeaderFooter(player);
            aboveNameService.setupPlayer(player);
        }

        getLogger().info(" ");
        getLogger().info("\u001B[33m ██████   █████  ██      ███████ ██████  ███    ███  ██████  ");
        getLogger().info("\u001B[33m ██   ██ ██   ██ ██      ██      ██   ██ ████  ████ ██    ██ ");
        getLogger().info("\u001B[33m ██████  ███████ ██      █████   ██████  ██ ████ ██ ██    ██ ");
        getLogger().info("\u001B[33m ██      ██   ██ ██      ██      ██   ██ ██  ██  ██ ██    ██ ");
        getLogger().info("\u001B[33m ██      ██   ██ ███████ ███████ ██   ██ ██      ██  ██████  ");
        getLogger().info("\u001B[33m                                                             ");
        getLogger().info("\u001B[32m              Plugin successfully enabled!                   ");
        getLogger().info("\u001B[37m              Version: " + getDescription().getVersion());
        getLogger().info("\u001B[0m ");
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
        if (tabListUpdateTask != null) {
            tabListUpdateTask.cancel();
        }

        prefixManager.save();

        getLogger().info(" ");
        getLogger().info("\u001B[33m ██████   █████  ██      ███████ ██████  ███    ███  ██████  ");
        getLogger().info("\u001B[33m ██   ██ ██   ██ ██      ██      ██   ██ ████  ████ ██    ██ ");
        getLogger().info("\u001B[33m ██████  ███████ ██      █████   ██████  ██ ████ ██ ██    ██ ");
        getLogger().info("\u001B[33m ██      ██   ██ ██      ██      ██   ██ ██  ██  ██ ██    ██ ");
        getLogger().info("\u001B[33m ██      ██   ██ ███████ ███████ ██   ██ ██      ██  ██████  ");
        getLogger().info("\u001B[33m                                                             ");
        getLogger().info("\u001B[31m              Plugin disabled. Goodbye!                      ");
        getLogger().info("\u001B[0m ");
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
        }, 200L, 200L);

        tabListUpdateTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            tabListService.updateAllHeadersFooters();
        }, 3L, 3L);
    }

    private void onAfkChange(Player player) {
        tabListService.updatePlayer(player);
        aboveNameService.updateTeam(player);
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

    public AboveNameService getAboveNameService() {
        return aboveNameService;
    }
}

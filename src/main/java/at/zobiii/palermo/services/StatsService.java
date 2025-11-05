package at.zobiii.palermo.services;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class StatsService {

    public int getDeaths(Player player) {
        try {
            return player.getStatistic(Statistic.DEATHS);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getPlayerKills(Player player) {
        try {
            return player.getStatistic(Statistic.PLAYER_KILLS);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getMobKills(Player player) {
        try {
            return player.getStatistic(Statistic.MOB_KILLS);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getDistanceWalked(Player player) {
        try {
            int total = 0;
            total += player.getStatistic(Statistic.WALK_ONE_CM);
            total += player.getStatistic(Statistic.SPRINT_ONE_CM);
            total += player.getStatistic(Statistic.CROUCH_ONE_CM);
            total += player.getStatistic(Statistic.SWIM_ONE_CM);
            total += player.getStatistic(Statistic.FLY_ONE_CM);
            total += player.getStatistic(Statistic.AVIATE_ONE_CM);
            total += player.getStatistic(Statistic.CLIMB_ONE_CM);
            return total / 100;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getJumps(Player player) {
        try {
            return player.getStatistic(Statistic.JUMP);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getTimeSinceDeath(Player player) {
        try {
            int deathTicks = player.getStatistic(Statistic.TIME_SINCE_DEATH);
            int seconds = deathTicks / 20;
            
            if (seconds < 60) {
                return seconds + "s";
            } else if (seconds < 3600) {
                int minutes = seconds / 60;
                return minutes + "m";
            } else {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                return hours + "h " + minutes + "m";
            }
        } catch (Exception e) {
            return "0s";
        }
    }

    public int getBlocksBroken(Player player) {
        try {
            int total = 0;
            for (Material material : Material.values()) {
                if (material.isBlock()) {
                    try {
                        total += player.getStatistic(Statistic.MINE_BLOCK, material);
                    } catch (Exception ignored) {
                    }
                }
            }
            return total;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getPlaytime(Player player) {
        try {
            int ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
            int minutes = ticks / 20 / 60;
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            
            if (hours > 0) {
                return hours + "h " + remainingMinutes + "m";
            } else {
                return remainingMinutes + "m";
            }
        } catch (Exception e) {
            return "0m";
        }
    }
}

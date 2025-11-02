package at.zobiii.palermo.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TpsTracker implements Runnable {

    private static final int SAMPLE_INTERVAL = 40;
    private static final long SEC_IN_NANO = 1000000000L;
    
    private final long[] tickSamples = new long[SAMPLE_INTERVAL];
    private int currentSample = 0;
    private long lastTick = System.nanoTime();

    public void start(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L);
    }

    @Override
    public void run() {
        long now = System.nanoTime();
        long timeSpent = now - lastTick;
        
        if (currentSample >= SAMPLE_INTERVAL) {
            currentSample = 0;
        }
        
        tickSamples[currentSample] = timeSpent;
        currentSample++;
        lastTick = now;
    }

    public double getTps() {
        if (currentSample == 0) {
            return 20.0;
        }
        
        long total = 0;
        int count = Math.min(currentSample, SAMPLE_INTERVAL);
        
        for (int i = 0; i < count; i++) {
            total += tickSamples[i];
        }
        
        double averageTickTime = (double) total / count;
        double tps = SEC_IN_NANO / averageTickTime;
        
        return Math.min(Math.round(tps * 10.0) / 10.0, 20.0);
    }
}

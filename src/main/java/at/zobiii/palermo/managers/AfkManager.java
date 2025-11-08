package at.zobiii.palermo.managers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class AfkManager {

    private static final long AFK_THRESHOLD = 120000;

    private final Map<UUID, Long> lastActivity;
    private final Map<UUID, Boolean> afkStatus;
    private Consumer<Player> onAfkChange;

    public AfkManager() {
        this.lastActivity = new ConcurrentHashMap<>();
        this.afkStatus = new ConcurrentHashMap<>();
    }

    public void setAfkChangeCallback(Consumer<Player> callback) {
        this.onAfkChange = callback;
    }

    public void registerActivity(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());

        if(isAfk(player)){
            setAfk(player, false);
        }
    }

    public void checkAfkStatus(Player player) {
        UUID uuid = player.getUniqueId();
        long lastSeen = lastActivity.getOrDefault(uuid, System.currentTimeMillis());
        long timeSinceActivity = System.currentTimeMillis() - lastSeen;

        boolean shouldBeAfk = timeSinceActivity >= AFK_THRESHOLD;
        boolean currentlyAfk = afkStatus.getOrDefault(uuid, false);

        if(shouldBeAfk && !currentlyAfk) {
            setAfk(player, true);
        }
    }

    public void setAfk(Player player, boolean afk){
        UUID uuid = player.getUniqueId();
        boolean wasAfk = afkStatus.getOrDefault(uuid, false);

        if(wasAfk != afk) {
            afkStatus.put(uuid, afk);
            
            if(afk) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.4f, 0.8f);
                spawnAfkParticles(player);
            } else {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.4f, 1.5f);
                spawnReturnParticles(player);
            }
            
            if(onAfkChange != null){
                onAfkChange.accept(player);
            }
        }
    }

    public boolean isAfk(Player player) {
        return afkStatus.getOrDefault(player.getUniqueId(), false);
    }

    public void removeAfk(UUID uuid) {
        lastActivity.remove(uuid);
        afkStatus.remove(uuid);
    }
    
    private void spawnAfkParticles(Player player) {
        Location loc = player.getLocation().add(0, 2.2, 0);
        player.getWorld().spawnParticle(Particle.SMOKE, loc, 8, 0.3, 0.1, 0.3, 0.02);
    }
    
    private void spawnReturnParticles(Player player) {
        Location loc = player.getLocation().add(0, 1.5, 0);
        player.getWorld().spawnParticle(Particle.FIREWORK, loc, 12, 0.3, 0.3, 0.3, 0.05);
    }
}

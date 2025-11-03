package at.zobiii.palermo.managers;

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
}

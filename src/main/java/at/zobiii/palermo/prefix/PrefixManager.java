package at.zobiii.palermo.prefix;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PrefixManager {
    private final Plugin plugin;
    private final File dataFile;
    private final Gson gson;
    private final Map<UUID, PrefixData> prefixes;

    public PrefixManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "prefixes.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.prefixes = new ConcurrentHashMap<>();
    }

    private void load() {
        if(!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            return;
        }

        try (FileReader reader = new FileReader(dataFile)){
            Type type = new TypeToken<Map<UUID, PrefixData>>(){}.getType();
            Map<UUID, PrefixData> loaded = gson.fromJson(reader, type);
            if(loaded != null) {
                prefixes.putAll(loaded);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load prefixes: " + e.getMessage());
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(prefixes, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save prefixes: " + e.getMessage());
        }
    }

    public void setPrefix(UUID uuid, String text, String color) {
        prefixes.put(uuid, new PrefixData(text, color));
        save();
    }

    public void removePrefix(UUID uuid) {
        prefixes.remove(uuid);
        save();
    }

    public PrefixData getPrefix(UUID uuid) {
        return prefixes.get(uuid);
    }

    public boolean hasPrefix(UUID uuid){
        return prefixes.containsKey(uuid);
    }

    public static class PrefixData {
        private final String text;
        private final String color;

        public PrefixData(String text, String color) {
            this.text = text;
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public String getColor() {
            return color;
        }
    }
}

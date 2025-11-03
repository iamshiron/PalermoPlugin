package at.zobiii.palermo.util;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil {

    private static final Map<String, ChatColor> COLOR_MAP = new HashMap<>();

    static {
        COLOR_MAP.put("black", ChatColor.BLACK);
        COLOR_MAP.put("dark_blue", ChatColor.DARK_BLUE);
        COLOR_MAP.put("dark_green", ChatColor.DARK_GREEN);
        COLOR_MAP.put("dark_aqua", ChatColor.DARK_AQUA);
        COLOR_MAP.put("dark_red", ChatColor.DARK_RED);
        COLOR_MAP.put("dark_purple", ChatColor.DARK_PURPLE);
        COLOR_MAP.put("gold", ChatColor.GOLD);
        COLOR_MAP.put("gray", ChatColor.GRAY);
        COLOR_MAP.put("grey", ChatColor.GRAY);
        COLOR_MAP.put("dark_gray", ChatColor.DARK_GRAY);
        COLOR_MAP.put("dark_grey", ChatColor.DARK_GRAY);
        COLOR_MAP.put("blue", ChatColor.BLUE);
        COLOR_MAP.put("green", ChatColor.GREEN);
        COLOR_MAP.put("aqua", ChatColor.AQUA);
        COLOR_MAP.put("red", ChatColor.RED);
        COLOR_MAP.put("light_purple", ChatColor.LIGHT_PURPLE);
        COLOR_MAP.put("pink", ChatColor.LIGHT_PURPLE);
        COLOR_MAP.put("yellow", ChatColor.YELLOW);
        COLOR_MAP.put("white", ChatColor.WHITE);
    }

    public static ChatColor parseColor(String input){
        if(input == null || input.isEmpty()){
            return ChatColor.WHITE;
        }

        String normalized = input.toLowerCase().trim();

        if(COLOR_MAP.containsKey(normalized)){
            return COLOR_MAP.get(normalized);
        }

        return ChatColor.WHITE;
    }
}

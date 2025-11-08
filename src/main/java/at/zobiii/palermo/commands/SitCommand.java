package at.zobiii.palermo.commands;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if(player.isInsideVehicle()) {
            player.sendMessage("§cYou are already sitting!");
            return true;
        }

        Location loc = player.getLocation();
        ArmorStand seat = loc.getWorld().spawn(loc, ArmorStand.class);
        seat.setVisible(false);
        seat.setGravity(false);
        seat.setInvulnerable(true);
        seat.setMarker(true);
        seat.addPassenger(player);

        loc.getWorld().spawnParticle(Particle.CLOUD, loc.add(0, 0.1, 0), 8, 0.3, 0.1, 0.3, 0.02);
        player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.3f, 2.0f);
        player.sendMessage("§aYou are now sitting! Shift to stand up");
        return true;
    }
}

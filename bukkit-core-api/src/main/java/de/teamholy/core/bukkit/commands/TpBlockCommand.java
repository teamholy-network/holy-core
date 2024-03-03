package de.teamholy.core.bukkit.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class TpBlockCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.hasPermission("*")) return false;

        Location loc = new Location(player.getWorld(), player.getLocation().getBlockX() + 0.5D, player.getLocation().getBlockY() + 0.5D, player.getLocation().getBlockZ() + 0.5D,player.getLocation().getYaw(),player.getLocation().getPitch());
        player.sendMessage("Teleport!");
        player.teleport(loc);
        return false;
    }
}

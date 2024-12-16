package de.teamholy.core.bukkit.commands;

import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class LocationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (!player.hasPermission("teamholy.location"))
                return false;
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    player.sendMessage(BukkitCore.PREFIX + "location " + args[1] + " set");
                    BukkitCore.getInstance().getLocationManager().addLocation(args[1],player.getLocation());
                }
            } else {
                player.sendMessage(BukkitCore.PREFIX + "/location set ("+ BukkitTranslateAPI.translate(player,"name") +")");
            }
        }
        return false;
    }
}

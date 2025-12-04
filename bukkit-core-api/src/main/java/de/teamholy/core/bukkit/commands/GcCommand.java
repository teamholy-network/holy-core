package de.teamholy.core.bukkit.commands;


import de.skydb.translateapi.bindings.BukkitTranslateAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class GcCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(
                ChatColor.RED + "This command can only be executed by a player.");
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("*"))
            return false;
        player.sendMessage("§7" + Runtime.getRuntime().freeMemory() / (1024 * 1024) + "§8 mb");
        player.sendMessage("§7"+ BukkitTranslateAPI.translate(player,"nach dem System.gc"));
        Runtime.getRuntime().gc();
        player.sendMessage("§7" + Runtime.getRuntime().freeMemory() / (1024 * 1024) + "§8 mb");
        player.sendMessage("§7Max memory : " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + "§8 mb");
        return false;
    }
}

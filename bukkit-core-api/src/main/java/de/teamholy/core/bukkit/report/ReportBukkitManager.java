package de.teamholy.core.bukkit.report;

import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.utils.Inventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class ReportBukkitManager implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (player.hasPermission("teamholy.team")) return false;

        Inventory inventory = new Inventory("§8» §6Perks Type",9*4);

        return false;
    }

    private void sendBungeeCommand(Player player, String command) {

    }
}

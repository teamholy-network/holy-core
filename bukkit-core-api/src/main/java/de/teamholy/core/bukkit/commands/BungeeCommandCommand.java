package de.teamholy.core.bukkit.commands;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.bukkit.BukkitCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* copyright by Yassino */
public class BungeeCommandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) return false;

        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < strings.length; a++) {
            if (a == (strings.length - 1)) {
                stringBuilder.append(strings[a]);
            } else {
                stringBuilder.append(strings[a] + " ");
            }
        }
        BukkitCore.getAPI().getCloudManager().sendCloudMessage("command","command", JsonDocument.newDocument("command",stringBuilder.toString()));

        return false;
    }
}

package de.teamholy.core.bungee.commands;

import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class OnlinetimeCommand extends Command {


    public OnlinetimeCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (args.length == 0) {
            showOnlineTime(proxiedPlayer, proxiedPlayer.getUniqueId());
        } else if (args.length == 1) {
            if (!proxiedPlayer.hasPermission("onlinetime.others")) return;
            UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[0]);
            if (uuid == null) {
                proxiedPlayer.sendMessage("§6OnlineTime §8× §7Diesen Spieler gibt es nicht");
                return;
            }

            showOnlineTime(proxiedPlayer, uuid);
        }
    }


    public void showOnlineTime(ProxiedPlayer proxiedPlayer, UUID uuid) {
        BungeeCore.getAPI().getPlayerService().getEntityAsync(uuid, () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(uuid), playerProfile -> {
            long millis = playerProfile.getOnlineTime();
            long hours = millis / 3600000L;
            long minT = millis - hours * 3600000L;
            long min = minT / 60000L;
            proxiedPlayer.sendMessage("§6OnlineTime §8× §7The onlinetime of " + BungeeCore.getAPI().getCloudManager().getColor(uuid) + BungeeCore.getAPI().getUuidManager().getName(uuid) + " §7is " + hours + "§6 hours §7and " + min + " §6minutes");
        });
    }
}

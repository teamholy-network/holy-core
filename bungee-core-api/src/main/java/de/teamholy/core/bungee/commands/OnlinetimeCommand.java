package de.teamholy.core.bungee.commands;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
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
                proxiedPlayer.sendMessage("§6"+"OnlineTime §8× §7"+ BungeeTranslateAPI.translate(proxiedPlayer,"Diesen Spieler gibt es nicht"));
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
            long min = minT / 60000L;                                                                                                 //"§6 hours §7and " + min + " §6minutes"
            proxiedPlayer.sendMessage("§6OnlineTime §8× §7"+BungeeTranslateAPI.translatePlaceholder(proxiedPlayer,"The onlinetime of {}§7 is {}§6 hours §7and {}§6 minutes" , BungeeCore.getAPI().getCloudManager().getColor(uuid) + BungeeCore.getAPI().getUuidManager().getName(uuid) ,String.valueOf(hours), String.valueOf(min)));
        });
    }
}

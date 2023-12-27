package de.teamholy.core.bungee.commands;

import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class DeletePlayerCommand extends Command {


    public DeletePlayerCommand() {
        super("resetcringe","*");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        if (args.length == 0) {
            proxiedPlayer.sendMessage("§c/resetcringe PLAYER");
        } else if (args.length == 1) {
            String name = args[0];
            UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(name);

            if (uuid == null) {
                proxiedPlayer.sendMessage("§cSpieler nicht gefunden!");
                return;
            }


            proxiedPlayer.sendMessage("§aBist du sicher das du die daten von " + BungeeCore.getAPI().getCloudManager().getColor(uuid) + name + " §clöschen willst?");
            proxiedPlayer.sendMessage("§cWenn ja dann schreib /resetcringe " + name + " confirm");
        } else if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            String name = args[0];
            UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(name);

            if (uuid == null) {
                proxiedPlayer.sendMessage("§cSpieler nicht gefunden!");
                return;
            }

            BungeeCore.getAPI().getPlayerService().getRepository().deleteById(uuid);
            BungeeCore.getAPI().getPlayerService().getRedisCache().remove(uuid);

            BungeeCore.getAPI().getGameService().getRepository().deleteById(uuid);
            BungeeCore.getAPI().getGameService().getRedisCache().remove(uuid);


            BungeeCore.getAPI().getPerkPlayerService().getRepository().deleteById(uuid);
            BungeeCore.getAPI().getPerkPlayerService().getRedisCache().remove(uuid);

            BungeeCore.getAPI().getStatsProfileService().getRedisCache().remove(uuid);
            BungeeCore.getAPI().getStatsProfileService().getRepository().deleteById(uuid);

            BungeeCore.getAPI().getClanPlayerService().getRepository().deleteById(uuid);
            BungeeCore.getAPI().getClanPlayerService().getRedisCache().remove(uuid);

            BungeeCore.getAPI().getFriendService().getRedisCache().remove(uuid);
            BungeeCore.getAPI().getFriendService().getRepository().deleteById(uuid);




            proxiedPlayer.sendMessage("§cDaten wurden erfolgreich gelöscht!");
        }
    }
}

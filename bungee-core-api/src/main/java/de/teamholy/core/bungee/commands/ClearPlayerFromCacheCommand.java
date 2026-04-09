package de.teamholy.core.bungee.commands;

//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class ClearPlayerFromCacheCommand extends Command {
    public ClearPlayerFromCacheCommand(String name, String... aliases) {
        super(name, "teamholy.clearcache", aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        if (args.length != 1) {
            sender.sendMessage("§c" + "Please use" + " /clearcache (" + "player" + ")");
            return;
        }

        UUID uuid = BungeeCore.getAPI().getUuidManager().getUUID(args[0]);
        if (uuid == null) {
            sender.sendMessage(BungeeUtil.format("§cThe player §e{} §cwas not found!", args[0]));
        }

        ProxiedPlayer player = BungeeCore.getInstance().getProxy().getPlayer(uuid);
        if (player != null) {
            player.disconnect("§cYou have been kicked from the network!");
        }

        BungeeCore.getAPI().getPlayerService().getRedisCache().remove(uuid);
        BungeeCore.getAPI().getGameService().getRedisCache().remove(uuid);
        BungeeCore.getAPI().getClanPlayerService().getRedisCache().remove(uuid);
        BungeeCore.getAPI().getPerkPlayerService().getRedisCache().remove(uuid);
        BungeeCore.getAPI().getFriendService().getRedisCache().remove(uuid);
    }
}

package de.teamholy.core.bungee.commands.friend;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class ReplyCommand extends Command {

    private String prefix = "§6MSG §8× §7";

    public ReplyCommand(String name) {
        super(name);
    }


    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (strings.length == 0) {
            player.sendMessage(prefix + "/r ("+ BungeeTranslateAPI.translate(player,"message")+")");
            return;
        }

        UUID target = FriendCommand.LASTREPLYS.get(player.getUniqueId());

        if (target == null) {
            player.sendMessage(prefix + BungeeTranslateAPI.translate(player,"This player does not exist"));
            return;
        }

        if (!BungeeCore.getAPI().getFriendManager().isFriend(player.getUniqueId(), target) && !player.hasPermission("teamholy.team")) {
            player.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(player,"You are not friends with {}",getColor(target) + getName(target)));
            return;
        }

        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(target);
        if (proxiedPlayer == null) {
            player.sendMessage(prefix + BungeeTranslateAPI.translate(player,"This player is not online!"));
            return;
        }


        if (target.toString().equals(player.getUniqueId().toString())) {
            player.sendMessage(prefix + BungeeTranslateAPI.translate(player,"You can't msg yourself!"));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < strings.length; a++) {
            stringBuilder.append(strings[a] + " ");
        }

        if (stringBuilder.toString().isEmpty()) {
            player.sendMessage(prefix + "/msg ("+BungeeTranslateAPI.translate(player,"player")+") ("+BungeeTranslateAPI.translate(player,"message")+")");
            return;
        }

        FriendCommand.msg(player.getUniqueId(), target, stringBuilder.toString());
    }


    private String getColor(UUID uuid) {
        return BungeeCore.getInstance().getPlayerColor(uuid);
    }

    private String getName(UUID uuid) {
        return BungeeCore.getAPI().getUuidManager().getName(uuid);
    }

}

package de.teamholy.core.bungee.commands.friend;

import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (strings.length == 0) {
            player.sendMessage(prefix + "/r (" + "message" + ")");
            return;
        }

        UUID target = FriendCommand.LASTREPLYS.get(player.getUniqueId());

        if (target == null) {
            player.sendMessage(prefix + "This player does not exist");
            return;
        }

        if (!BungeeCore.getAPI().getFriendManager().isFriend(player.getUniqueId(), target) && !player.hasPermission("teamholy.team")) {
            player.sendMessage(prefix + ("You are not friends with " + (getColor(target) + getName(target))));
            return;
        }

        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(target);
        if (proxiedPlayer == null) {
            player.sendMessage(prefix + "This player is not online!");
            return;
        }


        if (target.toString().equals(player.getUniqueId().toString())) {
            player.sendMessage(prefix + "You can't msg yourself!");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < strings.length; a++) {
            stringBuilder.append(strings[a] + " ");
        }

        if (stringBuilder.toString().isEmpty()) {
            player.sendMessage(prefix + "/msg (" + "player" + ") (" + "message" + ")");
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

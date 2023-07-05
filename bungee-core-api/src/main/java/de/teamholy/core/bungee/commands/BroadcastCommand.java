package de.teamholy.core.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class BroadcastCommand extends Command {

    public BroadcastCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.broadcast"))
            return;

        if (strings.length == 0) {
            player.sendMessage("§6Broadcast §8× §7/broadcast (message)");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < strings.length; a++) {
            stringBuilder.append(strings[a] + " ");
        }

        ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> {
            proxiedPlayer.sendMessage(" ");
            proxiedPlayer.sendMessage("§6Broadcast §8× §7" + stringBuilder.toString().replace("&","§"));
            proxiedPlayer.sendMessage(" ");
        });
    }
}

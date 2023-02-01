package de.teamholy.core.bungee.commands;

import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;
import java.util.UUID;

/* copyright by Yassino */
public class NickListCommand extends Command {
    public NickListCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("teamholy.team")) return;


        Map<UUID, String> nickMap = BungeeCore.getAPI().getNickManager().getNickList();

        if (nickMap.size() == 0) {
            player.sendMessage("§cThere are currently no nicked players");
        } else {
            player.sendMessage("§7There are currently §e" + nickMap.size() + " nicked §7users");
            player.sendMessage("");
            nickMap.forEach((uuid, s) -> {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
                player.sendMessage(" " + BungeeCore.getAPI().getCloudManager().getColor(uuid) + BungeeCore.getAPI().getUuidManager().getName(uuid) + " §8» §e" + s + " §8(§a" + proxiedPlayer.getServer().getInfo().getName() + "§8)");
            });
        }


    }
}

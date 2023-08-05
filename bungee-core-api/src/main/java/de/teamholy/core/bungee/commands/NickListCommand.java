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
        if (!sender.hasPermission("teamholy.team")) return;


        Map<UUID, String> nickMap = BungeeCore.getAPI().getNickManager().getNickList();

        if (nickMap.isEmpty()) {
            sender.sendMessage("§cThere are currently no nicked players");
        } else {
            sender.sendMessage("§7There are currently §e" + nickMap.size() + " nicked §7users");
            sender.sendMessage("");
            nickMap.forEach((uuid, s) -> {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
                if (proxiedPlayer != null) {
                    sender.sendMessage("§8- §e" + proxiedPlayer.getName() + " §8(§7" + s + "§8)");
                } else {
                    sender.sendMessage("§8- §e" + uuid + " §8(§7" + s + "§8)");
                }
            });
        }


    }
}

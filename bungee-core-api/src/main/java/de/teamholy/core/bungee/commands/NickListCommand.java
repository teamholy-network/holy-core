package de.teamholy.core.bungee.commands;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/* copyright by Yassino */
public class NickListCommand extends Command {
    public NickListCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("teamholy.team")) return;


        UUID author = BungeeUtil.parseAuthorUUID(sender);

        Map<UUID, String> nickMap = BungeeCore.getAPI().getNickManager().getNickList();

        Map<UUID, String> onlineNickMap = nickMap.entrySet()
            .stream()
            .filter(entry -> ProxyServer.getInstance().getPlayer(entry.getKey()) != null)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (onlineNickMap.isEmpty()) {
            sender.sendMessage("§c"+ BungeeTranslateAPI.translate(author, "There are currently no nicked players"));
        } else {
            sender.sendMessage(BungeeTranslateAPI.translatePlaceholder(author,"§7There are currently §e{} nicked §7users", String.valueOf(onlineNickMap.size())));
            sender.sendMessage("");
            onlineNickMap.forEach((uuid, s) -> {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
                if (proxiedPlayer != null) {
                    sender.sendMessage("§8- §e" + proxiedPlayer.getName() + " §8(§7" + s + "§8)");
                }
            });
        }
    }
}

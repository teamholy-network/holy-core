package de.teamholy.core.bungee.commands.punish;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.Console;
import java.util.UUID;

/* copyright by Yassino */
public class KickCommand extends SenderCommand {

    private String prefix = "§cKick §8× §7";


    public KickCommand(String[] commands, String permission) {
        super(commands, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {



        if (args.length == 0) {
            sender.sendMessage(prefix + "/kick (name) <reason>");
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {

            UUID nickUUID = BungeeCore.getAPI().getNickManager().getUUIDFromNick(args[0]);
            if (nickUUID == null) {
                sender.sendMessage(prefix + "This player is not online");
                return;
            }
            target = ProxyServer.getInstance().getPlayer(nickUUID);

        }


        String reason = "§crule violation §8/ §cwarning";
        if (args.length <= 2) {
            reason = args[1];
        }

        target.disconnect("§cYou have been kicked from the TeamHoly.DE network! \n§7Reason §8» " + reason);

        String kicked = BungeeCore.getAPI().getCloudManager().getColor(target.getUniqueId()) + target.getName();
        UUID author = BungeeUtil.parseAuthorUUID(sender);
        String name = BungeeCore.getAPI().getCloudManager().getColor(author) +BungeeCore.getAPI().getUuidManager().getName(author);

        BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(prefix + name + " §7kicked " + kicked + " §7for §c" + reason);
    }
}

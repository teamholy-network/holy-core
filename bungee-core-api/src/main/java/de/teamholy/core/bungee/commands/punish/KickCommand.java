package de.teamholy.core.bungee.commands.punish;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.SenderCommand;
import de.teamholy.core.bungee.util.BungeeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/* copyright by Yassino */
public class KickCommand extends SenderCommand {

    private String prefix = "§cKick §8× §7";


    public KickCommand(String[] commands, String permission) {
        super(commands, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        UUID author = BungeeUtil.parseAuthorUUID(sender);

        if (args.length == 0) {
            sender.sendMessage(prefix + "/kick ("+ BungeeTranslateAPI.translate(author,"name")+") <"+BungeeTranslateAPI.translate(author,"reason")+">");
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {

            UUID nickUUID = BungeeCore.getAPI().getNickManager().getUUIDFromNick(args[0]);
            if (nickUUID == null) {
                sender.sendMessage(prefix + BungeeTranslateAPI.translate(author,"This player is not online"));
                return;
            }
            target = ProxyServer.getInstance().getPlayer(nickUUID);

        }

        if (target.hasPermission("teamholy.team") && !sender.hasPermission("*")) {
            sender.sendMessage(prefix + BungeeTranslateAPI.translate(author,"you cant kick a §cteammember§4!"));
            return;
        }


        String reason = "§crule violation §8/ §cwarning";
        if (args.length >= 2) {

            StringBuilder stringBuilder = new StringBuilder();
            for (int a = 1; a < args.length; a++) {
                stringBuilder.append(args[a]).append(" ");
            }

            reason = stringBuilder.toString();
        }

        target.disconnect("§c"+BungeeTranslateAPI.translatePlaceholder(target,"You have been kicked from the {} network!","TeamHoly.DE")+" \n§7"+BungeeTranslateAPI.translatePlaceholder(target,"Reason")+" §8» §e" + reason);

        String kicked = BungeeCore.getAPI().getCloudManager().getColor(target.getUniqueId()) + target.getName();
        String name = BungeeCore.getAPI().getCloudManager().getColor(author) + BungeeCore.getAPI().getUuidManager().getName(author);

        //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(prefix + name + " §7kicked " + kicked + " §7for §c" + reason);
        for (ProxiedPlayer staffNotifyPlayer : BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers()) {
            staffNotifyPlayer.sendMessage(prefix + BungeeTranslateAPI.translatePlaceholder(staffNotifyPlayer,"{} got kicked by {} for {}", kicked+"§7", name+"§7", "§c"+reason+"§7"));
        }
    }
}

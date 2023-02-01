package de.teamholy.core.bungee.util;

import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/* copyright by Yassino */
public class BungeeUtil {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    public static boolean hasPermission(CommandSender sender, String perm) {
        return !(sender instanceof ProxiedPlayer) || sender.hasPermission(perm);
    }

    public static void sendNoPermission(CommandSender sender) {
        sender.sendMessage(Message.PREFIX + "§cYou don't have permission to do this.");
    }

    public static UUID parseAuthorUUID(CommandSender sender) {
        return sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : Punish.getConsoleUuid();
    }

    public static UUID parseTargetArgument(String target) {

        if (target.contains("-")) {
            try {
                return UUID.fromString(target);
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }

        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(target);
        if (proxiedPlayer != null && proxiedPlayer.isConnected())
            return proxiedPlayer.getUniqueId();

        return BungeeCore.getAPI().getUuidManager().getUUID(target);
    }

    public static String parseDate(long timeStamp) {
        return SIMPLE_DATE_FORMAT.format(new Date(timeStamp));
    }

}

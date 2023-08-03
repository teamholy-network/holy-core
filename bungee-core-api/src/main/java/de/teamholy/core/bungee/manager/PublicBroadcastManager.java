package de.teamholy.core.bungee.manager;


import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.Helpers;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/* copyright by Greg */

public class PublicBroadcastManager {

    public enum BroadcastType {JOINME, LINK, GENERAL}

    private Helpers helpers;

    public PublicBroadcastManager() {
        this.helpers = new Helpers();
    }

    public void sendPublicBroadcast(String message, BroadcastType type, ProxiedPlayer sender) {
        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
            switch (type) {
                case JOINME:
                    sendJoinMe(all, sender);
                    break;
                case LINK:
                    break;
                case GENERAL:
                    sendGeneral(all, message.replace("&", "§"));
                    break;
            }
        }
    }

    private void sendJoinMe(ProxiedPlayer receiver, ProxiedPlayer sender) {
        receiver.sendMessage("§8§m-------------§f§lJOINME§8§m----------------");
        receiver.sendMessage("     " + BungeeCore.getAPI().getCloudManager().getColor(sender.getUniqueId()) + sender.getName() + " §7is playing on §d" + sender.getServer().getInfo().getName());
        TextComponent joinMeMessage = new TextComponent("                     §aJoin Server           ");
        joinMeMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/joinme " + sender.getName()));
        joinMeMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Play with " + BungeeCore.getAPI().getCloudManager().getColor(sender.getUniqueId()) + sender.getName())));
        receiver.sendMessage(joinMeMessage);
        receiver.sendMessage("§8§m-----------------------------------");
    }

    private void sendGeneral(ProxiedPlayer receiver, String message) {
        receiver.sendMessage("  ");
        receiver.sendMessage(helpers.centerMessage("§8[ §c!§8 ] §6BROADCAST"));
        receiver.sendMessage(helpers.centerMessage("§7" + message.replace("&", "§")));
        receiver.sendMessage("  ");
    }


}


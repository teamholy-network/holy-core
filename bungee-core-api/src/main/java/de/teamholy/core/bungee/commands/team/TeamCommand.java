package de.teamholy.core.bungee.commands.team;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class TeamCommand extends Command {

    private String prefix = "§cTeam §8× §7";

    public TeamCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }


    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.team"))
            return;

        int i = 0;
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.hasPermission("teamholy.team")) {

                StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));

                String name = BungeeCore.getInstance().getPlayerColor(proxiedPlayer.getUniqueId()) + proxiedPlayer.getName();
                TextComponent message = new TextComponent(prefix + name + " §8(§a" + proxiedPlayer.getServer().getInfo().getName() + "§8)");
                String notify = " §8» §a✔";
                if (!staffProfile.isNotify()) notify = " §8» §c✘";

                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7"+ BungeeTranslateAPI.translate(player,"Notify status of")+" " + name + notify)));

                player.sendMessage(message);
                i++;
            }
        }
        player.sendMessage(prefix + BungeeTranslateAPI.translate(player,"Online team members")+" §8(§b" + i + "§8)");
    }

}

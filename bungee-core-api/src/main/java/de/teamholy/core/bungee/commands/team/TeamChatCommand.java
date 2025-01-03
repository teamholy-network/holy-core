package de.teamholy.core.bungee.commands.team;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class TeamChatCommand extends Command {

    public TeamChatCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.team")) return;

        StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(player.getUniqueId()));

        if (!staffProfile.isNotify()) {
            player.sendMessage("§c" + BungeeTranslateAPI.translate(player, "You are not logged in!") + " /notify");
            return;
        }

        if (!player.hasPermission("teamholy.team"))
            return;

        if (strings.length == 0) {
            player.sendMessage("§cTeamchat §8× §7/teamchat (" + BungeeTranslateAPI.translate(player, "message") + ")");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < strings.length; a++) {
            stringBuilder.append(strings[a] + " ");
        }

        BungeeCore.getInstance().getBungeePlayerManager().notifyStaff("§cTeamchat §8× " + BungeeCore.getAPI().getCloudManager().getColor(player.getUniqueId()) + player.getName() + " §8» §7" + stringBuilder.toString().replace("&", "§"));

    }

}

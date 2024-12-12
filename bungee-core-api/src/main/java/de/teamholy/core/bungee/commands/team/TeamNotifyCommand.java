package de.teamholy.core.bungee.commands.team;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/* copyright by Yassino */
public class TeamNotifyCommand extends Command {

    public TeamNotifyCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("teamholy.team")) return;


        StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(player.getUniqueId()));

        staffProfile.setNotify(!staffProfile.isNotify());
        BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, true);


        String name = BungeeCore.getAPI().getCloudManager().getColor(player.getUniqueId()) + player.getName();
        if (staffProfile.isNotify()) {
            //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff("§cTeam §8× " + name + " §7has logged §ain");
            BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffmember -> {
                staffmember.sendMessage("§cTeam §8× "+ BungeeTranslateAPI.translatePlaceholder(staffmember,"{}§7 has logged in", name));
            });
        } else {
            player.sendMessage("§cTeam §8× " + BungeeTranslateAPI.translatePlaceholder(player,"{}§7 has logged out", name));
            //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff("§cTeam §8× " + name + " §7has logged §cout");
            BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffmember -> {
                staffmember.sendMessage("§cTeam §8× "+ BungeeTranslateAPI.translatePlaceholder(staffmember,"{}§7 has logged out", name));
            });
        }

    }
}

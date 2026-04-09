package de.teamholy.core.bungee.commands.team;

//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import de.teamholy.core.bungee.util.BungeeUtil;

/* copyright by Yassino */
public class TeamNotifyCommand extends Command {

    public TeamNotifyCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.team")) return;


        StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(player.getUniqueId()));

        staffProfile.setNotify(!staffProfile.isNotify());
        BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, true);


        String name = BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName();
        if (staffProfile.isNotify()) {
            //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff("§cTeam §8× " + name + " §7has logged §ain");
            BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffmember -> {
                staffmember.sendMessage("§cTeam §8× " + BungeeUtil.format("{}§7 has logged in", name));
            });
        } else {
            player.sendMessage("§cTeam §8× " + BungeeUtil.format("{}§7 has logged out", name));
            //BungeeCore.getInstance().getBungeePlayerManager().notifyStaff("§cTeam §8× " + name + " §7has logged §cout");
            BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffmember -> {
                staffmember.sendMessage("§cTeam §8× " + BungeeUtil.format("{}§7 has logged out", name));
            });
        }

    }
}

package de.teamholy.core.bungee.commands.team;

import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.List;

/* copyright by Yassino */
public class AdminChatCommand extends Command {

    public static List<String> adminRanks = Arrays.asList("ADMIN", "MANAGER");

    public AdminChatCommand(String name) {
        super(name, "*", "adc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        // I'm executing this command as console, just produce the error here.
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission("teamholy.team")) {
            return;
        }

        StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(player.getUniqueId()));

        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (!adminRanks.contains(playerProfile.getRank())) {
            return;
        }

        if (!staffProfile.isNotify()) {
            player.sendMessage("§cYou are not logged in! /notify");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§f§kKLK§r §4§lADMINCHAT §f§kKLK §8× §7/adminchat (message)");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        BungeeCore.getInstance().getBungeePlayerManager().notifyAdmin("§f§kKLK§r §4§lADMINCHAT §f§kKLK§r §8× " + BungeeCore.getAPI().getCloudManager().getColor(player.getUniqueId()) + player.getName() + " §8» §7" + stringBuilder.toString().replace("&", "§"));


    }
}

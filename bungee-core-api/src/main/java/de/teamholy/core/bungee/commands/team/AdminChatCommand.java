package de.teamholy.core.bungee.commands.team;

//import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("teamholy.team")) return;

        StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(player.getUniqueId()));


        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (!adminRanks.contains(playerProfile.getRank())) {
            return;
        }

        if (!staffProfile.isNotify()) {
            player.sendMessage("§c" + "You are not logged in!" + " /notify");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§f§kKLK§r §4§lADMINCHAT §f§kKLK §8× §7/adminchat (" + "message" + ")");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int a = 0; a < args.length; a++) {
            stringBuilder.append(args[a] + " ");
        }

        BungeeCore.getInstance().getBungeePlayerManager().notifyAdmin("§f§kKLK§r §4§lADMINCHAT §f§kKLK§r §8× " + BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName() + " §8» §7" + stringBuilder.toString().replace("&", "§"));


    }
}

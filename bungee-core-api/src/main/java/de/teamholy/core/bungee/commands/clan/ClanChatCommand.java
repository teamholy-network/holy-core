package de.teamholy.core.bungee.commands.clan;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.constants.Message;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.utility.ClanRank;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ClanChatCommand extends Command {
    public ClanChatCommand(String name, String... aliases) {
        super(name, null, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(new ComponentBuilder().append("This command can only be executed by a player.").color(
                ChatColor.RED).create());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (strings.length == 0) {
            player.sendMessage(Message.CLAN_PREFIX + "/cc (" + BungeeTranslateAPI.translate(player, "message") + ")");
            return;
        }

        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(), () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));
        if (clanProfile == null) {
            player.sendMessage(Message.CLAN_PREFIX + "§7" + BungeeTranslateAPI.translate(player, "You don't have a clan!"));
            return;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            message.append(strings[i]).append(" ");
        }
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanProfile.getClanId());
        BungeeCore.getInstance().getBungeePlayerManager().sendClanMessage(clan, Message.CLAN_PREFIX + ClanRank.parsePrefix(clanProfile.getClanRank()) + "§l" + clanProfile.getClanRank().getFancy() + " " + BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName() + "§8 » §7" + message);
    }
}

package de.teamholy.core.bungee.commands.party;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.PartyManager;
import de.teamholy.core.bungee.model.Party;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

/* copyright by Yassino */
public class PartyChatCommand extends Command {


    private String prefix = "§5Party §8× §7";
    private final PartyManager partyManager = BungeeCore.getInstance().getPartyManager();

    public PartyChatCommand(String name, String... aliases) {
        super(name, null, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (args.length > 0 && args[0] != null) {

            Party party = partyManager.getPartyByPlayerUUID(player.getUniqueId());

            if (party == null) {
                player.sendMessage(prefix + BungeeTranslateAPI.translate(player,"You are not in a party!"));
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int amount = 0; amount < args.length; amount++) {
                sb.append(args[amount]).append(" ");
            }


            String name = getColor(player.getUniqueId()) + player.getName();
            party.getPartyPlayers().forEach(all -> {
                ProxiedPlayer partyPlayer = ProxyServer.getInstance().getPlayer(all);
                if (partyManager.isPartyLeader(player.getUniqueId())) {
                    partyPlayer.sendMessage(prefix + "§c§l"+BungeeTranslateAPI.translate(partyPlayer,"LEADER")+" " + name + " §8» §7" + sb.toString());
                } else {
                    partyPlayer.sendMessage(prefix + "§a§l"+BungeeTranslateAPI.translate(partyPlayer,"MEMBER")+" " + name + " §8» §7" + sb.toString());
                }
            });

        } else {
            player.sendMessage(prefix + "/partychat ("+BungeeTranslateAPI.translate(player,"message")+")");
        }
    }


    private String getColor(UUID uuid) {
        return BungeeCore.getAPI().getCloudManager().getColor(uuid);
    }

}

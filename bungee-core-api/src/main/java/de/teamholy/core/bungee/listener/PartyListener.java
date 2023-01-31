package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.model.Party;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;

/* copyright by Yassino */
public class PartyListener implements Listener {

    public PartyListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();
        BungeeCore.getInstance().getPartyManager().removePlayerFromParty(proxiedPlayer);
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Party party = BungeeCore.getInstance().getPartyManager().getPartyByPlayerUUID(player.getUniqueId());

        if (party == null) return;

        if (!BungeeCore.getInstance().getPartyManager().isPartyLeader(player.getUniqueId())) return;

        if (player.getServer().getInfo().getName().contains("Lobby")) return;

        if (player.getServer().getInfo().getName().contains("BW")) {
            ArrayList<String> uuids = new ArrayList<>();
            party.getPartyPlayers().forEach(partyPlayer -> uuids.add(partyPlayer.toString()));
            if (uuids.size() == 1) return;
            BungeeCore.getAPI().getCloudManager().sendCloudMessage(player.getServer().getInfo().getName().split("-")[0],"autoteam",JsonDocument.newDocument("players",uuids));
        }



        party.getPartyPlayers().forEach(all -> {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(all);
            proxiedPlayer.sendMessage("§5Party §8× §7The party is trying to join a §6" + player.getServer().getInfo().getName() + " §7server");
            proxiedPlayer.connect(player.getServer().getInfo());
        });
    }

}

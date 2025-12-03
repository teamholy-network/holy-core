package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.model.Party;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PartyListener handles various party-related events for players in a BungeeCord network.
 * It registers itself as an event listener and reacts to player disconnection
 * and server switch events to manage party functionality and synchronization.
 */
public class PartyListener implements Listener {

    public PartyListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        BungeeCore.getInstance().getPartyManager().removePlayerFromParty(player);
    }

    @EventHandler
    public void onSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();

        handleBedwarsLeave(event, player);
        handlePartyFollow(player);
    }

    private void handleBedwarsLeave(ServerSwitchEvent event, ProxiedPlayer player) {
        if (event.getFrom() == null) {
            return;
        }

        String fromServer = event.getFrom().getName().toLowerCase();
        String currentServer = player.getServer().getInfo().getName().toLowerCase();

        if (fromServer.contains("bw") && currentServer.contains("lobby")) {
            BungeeCore.getAPI().getCloudManager().sendCloudMessage(
                "bukkit",
                "cameFromBw",
                JsonDocument.newDocument("uuid", player.getUniqueId().toString())
            );
        }
    }

    private void handlePartyFollow(ProxiedPlayer player) {
        Party party = BungeeCore.getInstance().getPartyManager().getPartyByPlayerUUID(player.getUniqueId());

        if (party == null || !BungeeCore.getInstance().getPartyManager().isPartyLeader(player.getUniqueId())) {
            return;
        }

        String serverName = player.getServer().getInfo().getName();

        if (serverName.contains("Lobby")) {
            return;
        }

        if (serverName.contains("BW")) {
            handleBedwarsAutoTeam(party, serverName);
        }

        movePartyMembers(party, player, serverName);
    }

    private void handleBedwarsAutoTeam(Party party, String serverName) {
        List<String> uuids = party.getPartyPlayers().stream()
            .map(UUID::toString)
            .collect(Collectors.toList());

        if (uuids.size() <= 1) {
            return;
        }

        String gameType = serverName.split("-")[0];
        BungeeCore.getAPI().getCloudManager().sendCloudMessage(
            gameType,
            "autoteam",
            JsonDocument.newDocument("players", uuids)
        );
    }

    private void movePartyMembers(Party party, ProxiedPlayer leader, String serverName) {
        party.getPartyPlayers().forEach(memberId -> {
            ProxiedPlayer member = ProxyServer.getInstance().getPlayer(memberId);

            if (member != null) {
                member.sendMessage(new TextComponent("§5Party §8× §7" + BungeeTranslateAPI.translatePlaceholder(
                    member,
                    "The party is trying to join a {} §7server",
                    "§6" + serverName
                )));
                member.connect(leader.getServer().getInfo());
            }
        });
    }
}
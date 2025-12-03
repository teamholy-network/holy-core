package de.teamholy.core.bungee.manager;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.model.Party;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the creation, manipulation, and deletion of player parties in a multiplayer environment.
 * This class provides methods to handle party membership, invitations, leadership, and communication
 * within parties.
 *
 * Each party is uniquely identified by the UUID of its leader and contains a list of active members
 * and invited players. Parties support customizable maximum sizes and can be private or public.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartyManager {

    private static final String PARTY_PREFIX = "§5Party §8× §7";
    private static final String PARTY_DISBANDED_MESSAGE = "§5Party §8× §cSince the party leader has left the party has been deleted!";
    private static final String PARTY_LEFT_MESSAGE = "§5Party §8× §7You left the party!";

    Map<UUID, Party> parties;

    public PartyManager() {
        this.parties = new ConcurrentHashMap<>();
    }

    public void removePlayerFromParty(ProxiedPlayer player) {
        Party party = getPartyByPlayerUUID(player.getUniqueId());

        if (party == null) {
            return;
        }

        if (isPartyLeader(player.getUniqueId())) {
            disbandParty(player.getUniqueId(), party);
        } else {
            removePartyMember(player, party);
        }

        player.sendMessage(new TextComponent(PARTY_LEFT_MESSAGE));
    }

    private void disbandParty(UUID leaderUuid, Party party) {
        notifyAllMembers(party, PARTY_DISBANDED_MESSAGE);
        parties.remove(leaderUuid);
    }

    private void removePartyMember(ProxiedPlayer player, Party party) {
        party.getPartyPlayers().remove(player.getUniqueId());

        String playerName = getColoredPlayerName(player);
        String message = PARTY_PREFIX + playerName + " §7has left the party";

        notifyAllMembers(party, message);
    }

    private void notifyAllMembers(Party party, String message) {
        party.getPartyPlayers().forEach(memberId -> {
            ProxiedPlayer member = ProxyServer.getInstance().getPlayer(memberId);
            if (member != null) {
                member.sendMessage(new TextComponent(message));
            }
        });
    }

    public Party createParty(ProxiedPlayer leader, int maxSize) {
        Party party = new Party(maxSize);
        party.getPartyPlayers().add(leader.getUniqueId());
        parties.put(leader.getUniqueId(), party);
        return party;
    }

    public boolean addPlayerToParty(ProxiedPlayer player, Party party) {
        if (party.getMaxSize() != -1 && party.getPartyPlayers().size() >= party.getMaxSize()) {
            return false;
        }

        party.getPartyPlayers().add(player.getUniqueId());
        party.getInvitedPlayers().remove(player.getUniqueId());

        String playerName = getColoredPlayerName(player);
        String message = PARTY_PREFIX + playerName + " §7joined the party";
        notifyAllMembers(party, message);

        return true;
    }

    public void kickPlayerFromParty(ProxiedPlayer player, Party party) {
        party.getPartyPlayers().remove(player.getUniqueId());

        String playerName = getColoredPlayerName(player);
        String message = PARTY_PREFIX + playerName + " §7was kicked out of the party";
        notifyAllMembers(party, message);

        if (player.isConnected()) {
            player.sendMessage(new TextComponent(PARTY_PREFIX + "§cYou were kicked from the party!"));
        }
    }

    public boolean hasInvite(ProxiedPlayer player, UUID partyLeaderId) {
        Party party = getPartyByPlayerUUID(partyLeaderId);
        return party != null && party.getInvitedPlayers().contains(player.getUniqueId());
    }

    public void invitePlayer(UUID playerId, Party party) {
        party.getInvitedPlayers().add(playerId);
    }

    public void removeInvite(UUID playerId, Party party) {
        party.getInvitedPlayers().remove(playerId);
    }

    public boolean isPartyLeader(UUID playerId) {
        return parties.containsKey(playerId);
    }

    public UUID getPartyLeader(Party party) {
        return parties.entrySet().stream()
            .filter(entry -> entry.getValue().equals(party))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }

    public Party getPartyByPlayerUUID(UUID playerId) {
        Party leaderParty = parties.get(playerId);
        if (leaderParty != null) {
            return leaderParty;
        }

        return parties.values().stream()
            .filter(party -> party.getPartyPlayers().contains(playerId))
            .findFirst()
            .orElse(null);
    }

    public boolean isInParty(UUID playerId) {
        return getPartyByPlayerUUID(playerId) != null;
    }

    public boolean isPartyFull(Party party) {
        return party.getMaxSize() != -1 && party.getPartyPlayers().size() >= party.getMaxSize();
    }

    public int getPartySize(Party party) {
        return party.getPartyPlayers().size();
    }

    public boolean transferLeadership(UUID oldLeader, UUID newLeader) {
        Party party = parties.remove(oldLeader);

        if (party == null || !party.getPartyPlayers().contains(newLeader)) {
            return false;
        }

        party.getPartyPlayers().remove(newLeader);
        party.getPartyPlayers().add(0, newLeader);

        parties.put(newLeader, party);

        String message = PARTY_PREFIX + "§eParty leadership transferred!";
        notifyAllMembers(party, message);

        return true;
    }

    private String getColoredPlayerName(ProxiedPlayer player) {
        return BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName();
    }

    public void sendPartyMessage(Party party, String message) {
        notifyAllMembers(party, PARTY_PREFIX + message);
    }

    public void clearInvitations(Party party) {
        party.getInvitedPlayers().clear();
    }

    public int getActivePartyCount() {
        return parties.size();
    }

    public void cleanupExpiredInvites() {
        parties.values().forEach(party ->
            party.getInvitedPlayers().removeIf(invitedId ->
                ProxyServer.getInstance().getPlayer(invitedId) == null
            )
        );
    }
}
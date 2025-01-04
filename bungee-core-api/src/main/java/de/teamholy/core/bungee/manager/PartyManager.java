package de.teamholy.core.bungee.manager;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.model.Party;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/* copyright by Yassino */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartyManager {

    HashMap<UUID, Party> parties = new HashMap<>();

    public void removePlayerFromParty(ProxiedPlayer proxiedPlayer) {
        Party party = getPartyByPlayerUUID(proxiedPlayer.getUniqueId());
        if (party != null) {
            if (isPartyLeader(proxiedPlayer.getUniqueId())) {
                party.getPartyPlayers().forEach(all -> {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(all);
                    player.sendMessage("§5Party §8× §cSince the party leader has left the party has been deleted!");
                });
                parties.remove(proxiedPlayer.getUniqueId());
            } else {
                party.getPartyPlayers().remove(proxiedPlayer.getUniqueId());
                String name = BungeeCore.getInstance().getPlayerColor(proxiedPlayer.getUniqueId()) + proxiedPlayer.getName();
                party.getPartyPlayers().forEach(all -> {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(all);
                    player.sendMessage("§5Party §8× " + name + " §7has left the party");
                });
            }
            proxiedPlayer.sendMessage("§5Party §8× §7You left the party!");
        }
    }

    public boolean gotInvited(ProxiedPlayer proxiedPlayer, UUID targetPArty) {
        Party party = getPartyByPlayerUUID(targetPArty);
        return party.getInvitedPlayers().contains(proxiedPlayer.getUniqueId());
    }

    public boolean isPartyLeader(UUID uuid) {
        for (Map.Entry<UUID, Party> map : parties.entrySet()) {
            if (map.getKey().equals(uuid)) {
                return true;
            }
        }
        return false;
    }


    public Party getPartyByPlayerUUID(UUID uuid) {
        for (Map.Entry<UUID, Party> map : parties.entrySet()) {
            for (UUID player : map.getValue().getPartyPlayers()) {
                if (player.equals(uuid)) {
                    return map.getValue();
                }
            }
        }
        return null;
    }

}

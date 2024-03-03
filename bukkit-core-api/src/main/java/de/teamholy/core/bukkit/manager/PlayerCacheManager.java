package de.teamholy.core.bukkit.manager;

import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.npc.models.NPCPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
public class PlayerCacheManager {

    @Getter
    private HashMap<UUID, CachedBukkitPlayer> cachedPlayers = new HashMap<>();

    public record CachedBukkitPlayer(Player player, PlayerRank rank, NPCPlayer npcPlayer, Clan clan) {
    }



}

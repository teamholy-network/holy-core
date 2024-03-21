package de.teamholy.core.bukkit.manager;

import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bukkit.npc.models.NPCPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class PlayerCacheManager {

    private final HashMap<UUID, CachedBukkitPlayer> cachedPlayers = new HashMap<>();

    @Getter
    @AllArgsConstructor
    @Setter
    public static class CachedBukkitPlayer {
        private Player player;
        private PlayerRank rank;
        private NPCPlayer npcPlayer;
        private Clan clan;
        private PerkPlayerProfile perkPlayerProfile;
    }


}

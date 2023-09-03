package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import org.redisson.api.RScoredSortedSet;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
public class RankingManager {

    private CoreAPI coreAPI;
    private final HashMap<String, RScoredSortedSet> sortedSetHashMap = new HashMap<>();

    public RankingManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;

        for (Gamemodes gamemodes : Gamemodes.values()) {
            for (StatsType statsType : StatsType.values()) {
                RScoredSortedSet scoredSortedSet = coreAPI.
                    getRedissonManager().getRedissonClient().getScoredSortedSet(gamemodes.toString() + "_" + statsType.toString());

                sortedSetHashMap.put(scoredSortedSet.getName(), scoredSortedSet);
            }
        }

    }

    public int getRankFromUUID(Gamemodes gamemodes, StatsType statsType, UUID uuid) {
        if (sortedSetHashMap.get(gamemodes.toString() + "_" + statsType.toString()).contains(uuid))
            return sortedSetHashMap.get(gamemodes + "_" + statsType).revRank(uuid) + 1;
        return -1;
    }

    public UUID getUUIDFromRank(Gamemodes gamemodes, StatsType statsType, int rank) {
        try {
            return (UUID) sortedSetHashMap.get(gamemodes + "_" + statsType).entryRangeReversed((rank+1),(rank+1)).stream().toList().get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}

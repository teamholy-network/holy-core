package de.teamholy.core;

import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import org.redisson.api.RScoredSortedSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class RankingSortManager {

    public static HashMap<String, RScoredSortedSet> sortedSetHashMap;

    public RankingSortManager() {
        start();
        CloudModuleCore.getInstance().getService().scheduleAtFixedRate(() -> {
            List<GameProfile> gameProfiles = new ArrayList<>(CloudModuleCore.getCoreAPI().getGameService().getRedisCache().values());
            insertStats(gameProfiles);
        }, 30, 30, TimeUnit.SECONDS);
    }

    public void start() {
        long start = System.currentTimeMillis();
        CloudModuleCore.getInstance().getLogger().info("starting ranking cache process");
        sortedSetHashMap = new HashMap<>();

        for (Gamemodes gamemodes : Gamemodes.values()) {
            for (StatsType statsType : StatsType.values()) {
                RScoredSortedSet scoredSortedSet = CloudModuleCore.getCoreAPI().
                    getRedissonManager().getRedissonClient().getScoredSortedSet(gamemodes.toString() + "_" + statsType.toString());
                scoredSortedSet.clear();

                sortedSetHashMap.put(scoredSortedSet.getName(), scoredSortedSet);
            }
        }


        List<GameProfile> gameProfiles = CloudModuleCore.getCoreAPI().getGameService().getRepository().findAll();
        insertStats(gameProfiles);

        CloudModuleCore.getInstance().getLogger().info("finished ranking cache process in " + ((System.currentTimeMillis() - start) / 1000) + "s with " + gameProfiles.size() + " entries");
    }

    public void insertStats(List<GameProfile> gameProfiles) {
        for (GameProfile gameProfile : gameProfiles) {
            for (Gamemodes gamemodes : Gamemodes.values()) {
                if (gameProfile.exists(gamemodes.toString())) {
                    for (StatsType statsType : StatsType.values()) {
                        RScoredSortedSet sortedSet = sortedSetHashMap.get(gamemodes.toString() + "_" + statsType.toString());
                        int stat = (int) gameProfile.getStat(gamemodes.toString(), statsType, gamemodes.getRankingKey());
                        if (stat != 1000) {
                            sortedSet.addAsync(stat, gameProfile.getPlayerId());
                        }
                    }
                }
            }
        }
    }

}

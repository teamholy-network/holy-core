package de.teamholy.core;

import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.task.RankingSortTask;
import org.redisson.api.RScoredSortedSet;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class RankingSortManager {

    private HashMap<String, RScoredSortedSet> sortedSetHashMap;

    public RankingSortManager() {
        CloudModule.getCoreAPI().getExecutor().execute(() -> {


            long start = System.currentTimeMillis();
            CloudModule.getInstance().getLogger().info("starting ranking cache process");
            sortedSetHashMap = new HashMap<>();
            for (Gamemodes gamemodes : Gamemodes.values()) {
                for (StatsType statsType : StatsType.values()) {
                    RScoredSortedSet scoredSortedSet = CloudModule.getCoreAPI().
                            getRedissonManager().getRedissonClient().getScoredSortedSet(gamemodes.toString() + "_" + statsType.toString());
                    scoredSortedSet.clear();

                    sortedSetHashMap.put(scoredSortedSet.getName(), scoredSortedSet);
                }
            }


            long i = 0;
            for (GameProfile gameProfile : CloudModule.getCoreAPI().getGameService().getRepository().findAll()) {
                insertStats(gameProfile);
                i++;
            }

            CloudModule.getInstance().getLogger().info("finished ranking cache process in " + ((System.currentTimeMillis() - start) / 1000) +"s with " + i + " entries");

            CloudModule.getInstance().getService().scheduleAtFixedRate(new RankingSortTask(),30,30,TimeUnit.SECONDS);
        });
    }

    public void insertStats(GameProfile gameProfile) {

        for (Gamemodes gamemodes : Gamemodes.values()) {

            if (gameProfile.exists(gamemodes.toString())) {

                for (StatsType value : StatsType.values()) {
                    sortedSetHashMap.get(gamemodes.toString() + "_" + value.toString())
                            .add(gameProfile.getStat(gamemodes.toString(),value,gamemodes.getRankingKey()),gameProfile.getPlayerId());

                }
            }
        }

    }

}

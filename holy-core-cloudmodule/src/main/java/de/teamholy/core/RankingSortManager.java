package de.teamholy.core;

import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.task.RankingSortTask;
import org.bson.Document;
import org.bson.types.Binary;
import org.redisson.api.RScoredSortedSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/* copyright by Yassino */
public class RankingSortManager {

    private HashMap<String, RScoredSortedSet> sortedSetHashMap;

    public RankingSortManager() {
        start();
        CloudModuleCore.getInstance().getService().scheduleAtFixedRate(new RankingSortTask(), 2, 2, TimeUnit.MINUTES);
    }

    public void start() {

        sortedSetHashMap = new HashMap<>();


        for (Gamemodes gamemodes : Gamemodes.values()) {
            for (StatsType statsType : StatsType.values()) {
                RScoredSortedSet<UUID> scoredSortedSet = CloudModuleCore.getCoreAPI().
                        getRedissonManager().getRedissonClient().getScoredSortedSet(gamemodes.toString() + "_" + statsType.toString());
                scoredSortedSet.clear();

                sortedSetHashMap.put(scoredSortedSet.getName(), scoredSortedSet);
            }
        }




        long start = System.currentTimeMillis();
        CloudModuleCore.getInstance().getLogger().info("Started caching");
        CloudModuleCore.getInstance().getMongoManager().getMongoDatabase().getCollection("game_profile_collection").find().forEach(document -> {




            Document statsMap = document.get("statsMap",Document.class);

            for (Gamemodes gamemodes : Gamemodes.values()) {

                if (statsMap.containsKey(gamemodes.toString())) {

                    for (StatsType value : StatsType.values()) {

                        sortedSetHashMap.get(gamemodes.toString() + "_" + statsMap.toString())
                                .add(statsMap.get(gamemodes.toString(),Document.class).
                                        get(value.toString(),Document.class)
                                        .getLong(gamemodes.getRankingKey()),
                                        UUID.fromString(document.get("_id", Binary.class).toString()));

                    }

                }

            }

        });
        CloudModuleCore.getInstance().getLogger().info("end " + ( System.currentTimeMillis() - start ) / 1000 + "s");






    }

    public void insertStats(List<GameProfile> gameProfiles) {
        for (Gamemodes gamemodes : Gamemodes.values()) {
            for (StatsType value : StatsType.values()) {

                RScoredSortedSet sortedSet = sortedSetHashMap.get(gamemodes.toString() + "_" + value.toString());
                 gameProfiles.stream()
                            .filter(gameProfile -> gameProfile.exists(gamemodes.toString()))
                            .forEach(gameProfile -> sortedSet.add(gameProfile.getStat(gamemodes.toString(), value, gamemodes.getRankingKey()), gameProfile.getPlayerId()));

            }
        }
    }


}

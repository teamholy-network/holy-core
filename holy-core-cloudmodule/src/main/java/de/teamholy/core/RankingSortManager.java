package de.teamholy.core;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.task.StatsResetTask;
import org.redisson.api.RScoredSortedSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class RankingSortManager {

    public static HashMap<String, RScoredSortedSet> sortedSetHashMap = new HashMap<>();

    public RankingSortManager() {
        CloudModuleCore.getInstance().getService().execute(this::start);

        CloudModuleCore.getInstance().getService().scheduleAtFixedRate(() -> {
            List<GameProfile> gameProfiles = new ArrayList<>(CloudModuleCore.getCoreAPI().getGameService().getRedisCache().values());
            insertStats(gameProfiles);
        }, 30, 30, TimeUnit.SECONDS);
    }

    public void start() {
        long start = System.currentTimeMillis();
        CloudModuleCore.getInstance().getLogger().info("[!] Starting to load all profiles!");

        List<GameProfile> gameProfiles = CloudModuleCore.getCoreAPI().getGameService().getRepository().findAll();
        int totalProfiles = gameProfiles.size();
        int profilesPerUpdate = 10000;

        for (Gamemodes gamemodes : Gamemodes.values()) {
            for (StatsType statsType : StatsType.values()) {
                RScoredSortedSet scoredSortedSet = CloudModuleCore.getCoreAPI()
                    .getRedissonManager().getRedissonClient().getScoredSortedSet(gamemodes.toString() + "_" + statsType.toString());
                scoredSortedSet.clear();
                sortedSetHashMap.put(scoredSortedSet.getName(), scoredSortedSet);
            }
        }

        CloudModuleCore.getInstance().getLogger().info("[✔] Finished loading all profiles!");
        CloudModuleCore.getInstance().getLogger().info("[!] Starting ranking cache process");


        int processedProfiles = 0;
        for (GameProfile gameProfile : gameProfiles) {


            for (Gamemodes gamemodes : Gamemodes.values()) {
                if (gameProfile.exists(gamemodes.toString())) {
                    for (StatsType statsType : StatsType.values()) {
                        RScoredSortedSet sortedSet = sortedSetHashMap.get(gamemodes.toString() + "_" + statsType.toString());
                        int stat = (int) gameProfile.getStat(gamemodes.toString(), statsType, gamemodes.getRankingKey());
                        if (stat > 1000) sortedSet.add(stat, gameProfile.getPlayerId());
                    }
                }
            }
            processedProfiles++;
            if (processedProfiles % profilesPerUpdate == 0 || processedProfiles == totalProfiles) {
                double progress = (double) processedProfiles / totalProfiles * 100;
                CloudModuleCore.getInstance().getLogger().info("[!] Processed profiles: " + processedProfiles + "/" + totalProfiles + " (" + String.format("%.2f", progress) + "%)");
            }
        }

        CloudModuleCore.getInstance().getLogger().info("[✔] Finished ranking cache process in " + ((System.currentTimeMillis() - start) / 1000) + "s with " + gameProfiles.size() + " entries");
        CloudModuleCore.getInstance().getLogger().info("[✔] Starting statsreset task with 60s interval");
        CloudModuleCore.getInstance().getService().scheduleAtFixedRate(new StatsResetTask(), 60, 60, TimeUnit.SECONDS);

    }


    public void insertStats(List<GameProfile> gameProfiles) {
        for (GameProfile gameProfile : gameProfiles) {
            for (Gamemodes gamemodes : Gamemodes.values()) {
                if (gameProfile.exists(gamemodes.toString())) {
                    for (StatsType statsType : StatsType.values()) {
                        RScoredSortedSet sortedSet = sortedSetHashMap.get(gamemodes.toString() + "_" + statsType.toString());
                        int stat = (int) gameProfile.getStat(gamemodes.toString(), statsType, gamemodes.getRankingKey());
                        if (stat > 1000) sortedSet.add(stat, gameProfile.getPlayerId());
                    }
                }
            }
        }
    }

}

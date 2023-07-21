package de.teamholy.core.task;

import com.google.common.collect.Lists;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.RankingSortManager;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.GameRepository;
import de.teamholy.core.api.entities.game.GameService;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import eu.koboo.en2do.repository.methods.fields.FieldUpdate;
import eu.koboo.en2do.repository.methods.fields.UpdateBatch;
import org.redisson.api.RMapCache;
import org.redisson.api.RScoredSortedSet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/* copyright by Yassino */
public class StatsResetTask implements Runnable {


    private final String monthly_statsreset = "01";
    private final String daily_statsreset = "00";

    @Override
    public void run() {

        Long currentTime = System.currentTimeMillis();


        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:dd");
        Date timeDate = new Date(currentTime);
        String time = timeFormat.format(timeDate);

        String day = time.split(":")[1];
        String hour = time.split(":")[0];

        if (CloudModuleCore.DAILY && !hour.equalsIgnoreCase(daily_statsreset)) CloudModuleCore.DAILY = false;
        if (CloudModuleCore.MONTHLY && !day.equalsIgnoreCase(monthly_statsreset)) CloudModuleCore.MONTHLY = false;


        if (hour.equalsIgnoreCase(daily_statsreset) && !CloudModuleCore.DAILY)  {
            resetStatsFromGameProfiles(StatsType.DAILY);
            System.out.println("test 2");
        }
        if (day.equalsIgnoreCase(monthly_statsreset) && !CloudModuleCore.MONTHLY) {
            resetStatsFromGameProfiles(StatsType.MONTHLY);
        }

    }


    private void resetStatsFromGameProfiles(StatsType statsType) {
        CloudModuleCore.getCoreAPI().getExecutor().execute(() -> {

            /*
            Reset all in cache
             */
            RMapCache<UUID, GameProfile> rMapCache = CloudModuleCore.getCoreAPI().getGameService().getRedisCache();
            rMapCache.values().forEach(gameProfile -> {
                for (Gamemodes gamemode : Gamemodes.values()) {
                    gamemode.getStatKeys().forEach(s -> gameProfile.setStat(gamemode.toString(),statsType,s,0));
                }
                boolean forceCache = rMapCache.remainTimeToLive(gameProfile.getPlayerId()) == -1;
                CloudModuleCore.getCoreAPI().getGameService().saveEntity(gameProfile, forceCache, true);
            });

            /*
            reset all in db
             */
            List<FieldUpdate> fieldUpdateList = Lists.newLinkedList();
            for (Gamemodes value : Gamemodes.values()) {
                if (value.getStatKeys().size() != 1 && !value.getStatKeys().get(0).isEmpty()) {
                    for (String statKey : value.getStatKeys()) {
                        fieldUpdateList.add(FieldUpdate.set("statsMap." + value + "." + statsType + "." +  statKey,0));
                    }
                }
            }
            CloudModuleCore.getCoreAPI().getGameService().getRepository().updateAllFields(UpdateBatch.of(fieldUpdateList));



            for (Gamemodes gamemodes : Gamemodes.values()) {
                RScoredSortedSet sortedSet = RankingSortManager.sortedSetHashMap.get(gamemodes.toString() + "_" + statsType.toString());
                sortedSet.clear();
            }

            

            if (statsType == StatsType.DAILY) CloudModuleCore.DAILY = true;
            else if (statsType == StatsType.MONTHLY) CloudModuleCore.MONTHLY = true;

            CloudModuleCore.getCoreAPI().getCloudManager().sendCloudMessage("proxy","statsreset",new JsonDocument("statsType",statsType));

            System.out.println("STATSRESET - " + statsType);

        });

    }

}

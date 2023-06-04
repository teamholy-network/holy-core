package de.teamholy.core.task;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;
import org.redisson.api.RMapCache;

import java.text.SimpleDateFormat;
import java.util.Date;

/* copyright by Yassino */
public class StatsResetTask implements Runnable {

    @Override
    public void run() {

        Long currentTime = System.currentTimeMillis();


        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:dd");
        Date timeDate = new Date(currentTime);
        String time = timeFormat.format(timeDate);

        String day = time.split(":")[1];
        String hour = time.split(":")[0];

        if (CloudModuleCore.DAILY && !hour.equalsIgnoreCase("00")) CloudModuleCore.DAILY = false;
        if (CloudModuleCore.MONTHLY && !day.equalsIgnoreCase("01")) CloudModuleCore.MONTHLY = false;


        boolean update = false;
        if (hour.equalsIgnoreCase("00") && !CloudModuleCore.DAILY)  {
            resetStatsFromGameProfiles(StatsType.DAILY);
            update = true;
        }
        if (day.equalsIgnoreCase("01") && !CloudModuleCore.MONTHLY) {
            resetStatsFromGameProfiles(StatsType.MONTHLY);
            update = true;
        }

        if (update) CloudModuleCore.getInstance().getSortManager().start();

    }


    private void resetStatsFromGameProfiles(StatsType statsType) {
        CloudModuleCore.getCoreAPI().getExecutor().execute(() -> {

            RMapCache rMapCache = CloudModuleCore.getCoreAPI().getGameService().getRedisCache();


            CloudModuleCore.getCoreAPI().getGameService().getRedisCache().values().forEach(gameProfile -> {


                resetStatsCache
                        (gameProfile,statsType);

                boolean forceCache = rMapCache.remainTimeToLive(gameProfile.getPlayerId()) == -1;


                CloudModuleCore.getCoreAPI().getGameService().saveEntity(gameProfile, forceCache, true);


            });


            if (statsType == StatsType.DAILY) CloudModuleCore.DAILY = true;
            else if (statsType == StatsType.MONTHLY) CloudModuleCore.MONTHLY = true;

            CloudModuleCore.getCoreAPI().getCloudManager().sendCloudMessage("proxy","statsreset",new JsonDocument("statsType",statsType));

            CloudModuleCore.getInstance().getLogger().info("         Statsreset         " + statsType.toString());

        });

    }

    private void resetStatsCache(GameProfile gameProfile, StatsType statsType) {
/*        for (Gamemodes gamemode : Gamemodes.values()) {
            gamemode.getStatKeys().forEach(s -> gameProfile.setStat(gamemode.toString(),statsType,s,0));
        }*/
    }

}

package de.teamholy.core.task;

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


        if (hour.equalsIgnoreCase("00") && !CloudModuleCore.DAILY) resetStatsFromGameProfiles(StatsType.DAILY);
        if (day.equalsIgnoreCase("01") && !CloudModuleCore.MONTHLY) resetStatsFromGameProfiles(StatsType.MONTHLY);
    }


    private void resetStatsFromGameProfiles(StatsType statsType) {
        CloudModuleCore.getCoreAPI().getExecutor().execute(() -> {

            RMapCache rMapCache = CloudModuleCore.getCoreAPI().getGameService().getRedisCache();


            CloudModuleCore.getCoreAPI().getGameService().getRepository().findAll().forEach(gameProfile -> {


                resetStats(gameProfile,statsType);

                if (rMapCache.containsKey(gameProfile.getPlayerId())) {

                    boolean forceCache = rMapCache.remainTimeToLive(gameProfile.getPlayerId()) == -1;


                    CloudModuleCore.getCoreAPI().getGameService().saveEntity(gameProfile, forceCache, true);

                } else {
                    CloudModuleCore.getCoreAPI().getGameService().getRepository().save(gameProfile);
                }

            });


            if (statsType == StatsType.DAILY) CloudModuleCore.DAILY = true;
            else if (statsType == StatsType.MONTHLY) CloudModuleCore.MONTHLY = true;

            CloudModuleCore.getInstance().getPlayerManager().onlinePlayers().asPlayers().forEach(iCloudPlayer -> {
                iCloudPlayer.getPlayerExecutor().sendChatMessage("       §f§lSTATSRESET     ");
                iCloudPlayer.getPlayerExecutor().sendChatMessage("§7The " + statsType.toBeauty() + " §7stats have been reset");
            });

            CloudModuleCore.getInstance().getLogger().info("         Statsreset         " + statsType.toString());

        });

        CloudModuleCore.getInstance().getSortManager().start();

    }

    private void resetStats(GameProfile gameProfile, StatsType statsType) {
        for (Gamemodes gamemode : Gamemodes.values()) {
            gamemode.getStatKeys().forEach(s -> gameProfile.setStat(gamemode.toString(),statsType,s,0));
        }
    }

}

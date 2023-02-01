package de.teamholy.core.task;

import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.teamholy.core.CloudModule;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.manager.CloudManager;
import de.teamholy.core.api.utility.Gamemodes;

import java.text.SimpleDateFormat;
import java.util.Date;

/* copyright by Yassino */
public class StatsResetTask implements Runnable {

    @Override
    public void run() {
        CloudModule.getInstance().getLogger().info("Started Stats reset Task!");


        Long currentTime = System.currentTimeMillis();


        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:dd");
        Date timeDate = new Date(currentTime);
        String time = timeFormat.format(timeDate);

        String day = time.split(":")[1];
        String hour = time.split(":")[0];

        if (CloudModule.DAILY && !hour.equalsIgnoreCase("00")) CloudModule.DAILY = false;
        if (CloudModule.MONTHLY && !day.equalsIgnoreCase("01")) CloudModule.MONTHLY = false;


        if (hour.equalsIgnoreCase("00") && !CloudModule.DAILY) resetStatsFromGameProfiles(StatsType.DAILY);
        if (hour.equalsIgnoreCase("01") && !CloudModule.MONTHLY) resetStatsFromGameProfiles(StatsType.MONTHLY);
    }


    private void resetStatsFromGameProfiles(StatsType statsType) {
        CloudModule.getCoreAPI().getExecutor().execute(() -> {

            CloudModule.getCoreAPI().getGameService().getRepository().findAll().forEach(gameProfile -> {

                boolean forceCache = CloudModule.getCoreAPI().getGameService().getRedisCache().remainTimeToLive(gameProfile.getPlayerId()) == -1;

                resetStats(gameProfile,statsType);

                CloudModule.getCoreAPI().getGameService().saveEntity(gameProfile,forceCache,true);
            });


            if (statsType == StatsType.DAILY) CloudModule.DAILY = true;
            else if (statsType == StatsType.MONTHLY) CloudModule.MONTHLY = true;

            CloudModule.getInstance().getPlayerManager().onlinePlayers().asPlayers().forEach(iCloudPlayer -> {
                iCloudPlayer.getPlayerExecutor().sendChatMessage("       §f§lSTATSRESET     ");
                iCloudPlayer.getPlayerExecutor().sendChatMessage("§7The " + statsType.toBeauty() + " §7stats have been reset");
            });
        });
    }

    private void resetStats(GameProfile gameProfile, StatsType statsType) {
        for (Gamemodes gamemode : Gamemodes.values()) {
            gamemode.getStatKeys().forEach(s -> gameProfile.setStat(gamemode.toString(),statsType,s,0));
        }
    }

}

package de.teamholy.core.task;

import de.teamholy.core.CloudModule;
import de.teamholy.core.RankingSortManager;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.api.utility.Gamemodes;

/* copyright by Yassino */
public class RankingSortTask implements Runnable{
    @Override
    public void run() {
        CloudModule.getInstance().getLogger().info("Started RankingSort Task!");


        CloudModule.getCoreAPI().getGameService().getRedisCache().forEach((uuid, gameProfile) -> {
            CloudModule.getInstance().getSortManager().insertStats(gameProfile);
        });

    }

}

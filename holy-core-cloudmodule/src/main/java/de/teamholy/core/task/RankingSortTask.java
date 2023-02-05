package de.teamholy.core.task;

import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.api.entities.game.GameProfile;

import java.util.ArrayList;
import java.util.List;

/* copyright by Yassino */
public class RankingSortTask implements Runnable{
    @Override
    public void run() {

        List<GameProfile> gameProfiles = new ArrayList<>(CloudModuleCore.getCoreAPI().getGameService().getRedisCache().values());
        CloudModuleCore.getInstance().getSortManager().insertStats(gameProfiles);

    }

}

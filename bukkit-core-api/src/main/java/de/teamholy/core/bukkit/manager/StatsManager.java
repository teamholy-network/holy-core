package de.teamholy.core.bukkit.manager;


import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.game.StatsType;
import de.teamholy.core.bukkit.BukkitCore;

import java.text.DecimalFormat;
import java.util.UUID;

/* copyright by Yassino */
public class StatsManager {


    public void addStat(String game, String key, UUID uuid) {
        GameProfile gameProfile = BukkitCore.getAPI().getGameService().getEntity(uuid, () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(uuid));
        gameProfile.addStat(game, StatsType.DAILY, key, 1);
        gameProfile.addStat(game, StatsType.MONTHLY, key, 1);
        gameProfile.addStat(game, StatsType.ALLTIME, key, 1);
        BukkitCore.getAPI().getGameService().saveEntity(gameProfile, true, true);
    }

    public int handleTrophie(UUID uuid, String game, TrophieAdjustType adjustType, int trophies) {


        GameProfile gameProfile = BukkitCore.getAPI().getGameService().getEntity(uuid, () -> BukkitCore.getAPI().getGameService().getRepository().findFirstById(uuid));
        if (adjustType == TrophieAdjustType.PLUS) {
            gameProfile.addStat(game, StatsType.DAILY, "trophies", trophies);
            gameProfile.addStat(game, StatsType.MONTHLY, "trophies", trophies);
            gameProfile.addStat(game, StatsType.ALLTIME, "trophies", trophies);
        } else {
            gameProfile.removeStat(game, StatsType.DAILY, "trophies", trophies);
            gameProfile.removeStat(game, StatsType.MONTHLY, "trophies", trophies);
            gameProfile.removeStat(game, StatsType.ALLTIME, "trophies", trophies);
        }
        BukkitCore.getAPI().getGameService().saveEntity(gameProfile, true, true);

        return trophies;
    }


    public String calculateKD(long kills, long deaths) {
        String KD;
        if (kills != 0 && deaths != 0) {
            double killsdeaths = (double) kills / (double) deaths;
            KD = (new DecimalFormat("0.00")).format(killsdeaths);
        } else {
            KD = "0.00";
        }
        return KD;
    }


    public enum TrophieAdjustType {
        PLUS, MINUS;
    }

}

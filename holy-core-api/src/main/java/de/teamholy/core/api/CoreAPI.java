package de.teamholy.core.api;

import de.teamholy.core.api.entities.clan.ClanService;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerService;
import de.teamholy.core.api.entities.friend.FriendService;
import de.teamholy.core.api.entities.player.PlayerService;
import de.teamholy.core.api.entities.punish.PunishService;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryService;
import de.teamholy.core.api.entities.skin.SkinService;
import de.teamholy.core.api.entities.stats.StatsService;
import de.teamholy.core.api.manager.CloudManager;
import de.teamholy.core.api.manager.RedissonManager;
import de.teamholy.core.api.manager.ReportManager;
import de.teamholy.core.api.manager.UUIDManager;
import eu.koboo.en2do.MongoManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class CoreAPI {

    MongoManager mongoManager;
    RedissonManager redissonManager;
    CloudManager cloudManager;
    ReportManager reportManager;
    UUIDManager uuidManager;
    ExecutorService executor;

    PlayerService playerService;
    PunishService punishService;
    StatsService statsService;
    FriendService friendService;
    SkinService skinService;
    PunishHistoryService punishHistoryService;
    ClanService clanService;
    ClanPlayerService clanPlayerService;

    public CoreAPI() {
        this.mongoManager = new MongoManager();
        this.redissonManager = new RedissonManager(this);
        this.cloudManager = new CloudManager(this);
        this.uuidManager = new UUIDManager(this);
        this.reportManager = new ReportManager(this);
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        this.playerService = new PlayerService(this);
        this.punishService = new PunishService(this);
        this.statsService = new StatsService(this);
        this.friendService = new FriendService(this);
        this.skinService = new SkinService(this);
        this.punishHistoryService = new PunishHistoryService(this);
        this.clanService = new ClanService(this);
        this.clanPlayerService = new ClanPlayerService(this);

    }

    public void onEnable() {
        if (redissonManager != null) {
            redissonManager.onEnable();
        }
    }

    public void onDisable() {
        mongoManager.close();
        if (redissonManager != null) {
            redissonManager.onDisable();
        }
    }
}

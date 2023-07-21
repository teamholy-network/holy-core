package de.teamholy.core.api;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.teamholy.core.api.entities.ban.BanService;
import de.teamholy.core.api.entities.clan.ClanService;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerService;
import de.teamholy.core.api.entities.friend.FriendService;
import de.teamholy.core.api.entities.game.GameService;
import de.teamholy.core.api.entities.mute.MuteService;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerService;
import de.teamholy.core.api.entities.player.PlayerService;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryService;
import de.teamholy.core.api.entities.skin.SkinService;
import de.teamholy.core.api.entities.staff.StaffService;
import de.teamholy.core.api.manager.*;
import eu.koboo.en2do.Credentials;
import eu.koboo.en2do.MongoManager;
import eu.koboo.en2do.repository.methods.fields.FieldUpdate;
import eu.koboo.en2do.repository.methods.fields.UpdateBatch;
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
    ClanManager clanManager;
    ReportManager reportManager;
    UUIDManager uuidManager;
    CoinManager coinManager;
    NickManager nickManager;
    StaffManager staffManager;
    FriendManager friendManager;
    RankingManager rankingManager;
    ExecutorService executor;

    PlayerService playerService;
    BanService banService;
    MuteService muteService;
    GameService gameService;
    FriendService friendService;
    SkinService skinService;
    PunishHistoryService punishHistoryService;
    ClanService clanService;
    ClanPlayerService clanPlayerService;
    StaffService staffService;
    PerkPlayerService perkPlayerService;
    ConfigManager config;

    public CoreAPI() {
        CloudManager cloudManager1;

        this.config = new ConfigManager();
        this.mongoManager = new MongoManager(Credentials.
                of("mongodb://" + config.getUsername() + ":" + config.getPassword() + "@" + config.getHost() + ":" + config.getPort() + "/?authSource=admin"
                        , "holy"));


        this.redissonManager = new RedissonManager(this);


        this.playerService = new PlayerService(this);
        this.banService = new BanService(this);
        this.gameService = new GameService(this);
        this.friendService = new FriendService(this);
        this.skinService = new SkinService(this);
        this.punishHistoryService = new PunishHistoryService(this);
        this.clanService = new ClanService(this);
        this.clanPlayerService = new ClanPlayerService(this);
        this.staffService = new StaffService(this);
        this.nickManager = new NickManager(this);
        this.muteService = new MuteService(this);
        this.perkPlayerService = new PerkPlayerService(this);


        try {
            cloudManager1 = new CloudManager(this, CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class));
        } catch (NoClassDefFoundError error) {
            cloudManager1 = new CloudManager(this, null);
        }
        this.cloudManager = cloudManager1;
        this.uuidManager = new UUIDManager(this);
        this.reportManager = new ReportManager(this);
        this.clanManager = new ClanManager(this, clanService);
        this.coinManager = new CoinManager(this);
        this.staffManager = new StaffManager(this);
        this.friendManager = new FriendManager(this);
        this.rankingManager = new RankingManager(this);
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    }


    public CoreAPI(Credentials credentials) {
        CloudManager cloudManager1;

        this.config = new ConfigManager();
        this.mongoManager = new MongoManager(credentials);


        this.redissonManager = new RedissonManager(this);


        this.playerService = new PlayerService(this);
        this.banService = new BanService(this);
        this.gameService = new GameService(this);
        this.friendService = new FriendService(this);
        this.skinService = new SkinService(this);
        this.punishHistoryService = new PunishHistoryService(this);
        this.clanService = new ClanService(this);
        this.clanPlayerService = new ClanPlayerService(this);
        this.staffService = new StaffService(this);
        this.nickManager = new NickManager(this);
        this.muteService = new MuteService(this);
        this.perkPlayerService = new PerkPlayerService(this);


        try {
            cloudManager1 = new CloudManager(this, CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class));
        } catch (NoClassDefFoundError error) {
            cloudManager1 = new CloudManager(this, null);
        }

        this.cloudManager = cloudManager1;
        this.uuidManager = new UUIDManager(this);
        this.reportManager = new ReportManager(this);
        this.clanManager = new ClanManager(this, clanService);
        this.coinManager = new CoinManager(this);
        this.staffManager = new StaffManager(this);
        this.friendManager = new FriendManager(this);
        this.rankingManager = new RankingManager(this);
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    }

    public void onDisable() {
        mongoManager.close();
        if (redissonManager != null) {
            redissonManager.onDisable();
        }
    }
}

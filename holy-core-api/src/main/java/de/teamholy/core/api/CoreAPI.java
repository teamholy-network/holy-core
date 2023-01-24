package de.teamholy.core.api;

import de.teamholy.core.api.entities.player.PlayerService;
import de.teamholy.core.api.manager.RedissonManager;
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
    ExecutorService executor;

    PlayerService playerService;

    public CoreAPI() {
        this.mongoManager = new MongoManager();
        this.redissonManager = new RedissonManager(this);
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        this.playerService = new PlayerService(this);
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

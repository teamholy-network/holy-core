package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedissonManager {

    @NotNull
    CoreAPI plugin;

    @NotNull
    Config config;

    @NonFinal
    RedissonClient redissonClient;

    public RedissonManager(@NotNull CoreAPI plugin) {
        this.plugin = plugin;
        this.config = new Config();

        config.useSingleServer().setAddress("");
        config.useSingleServer().setRetryAttempts(3);
        config.useSingleServer().setTimeout(10000);
        config.useSingleServer().setConnectionPoolSize(500);
        config.useSingleServer().setRetryInterval(2000);
        this.redissonClient = Redisson.create();
    }


    public void onDisable() {
        if (config == null) {
            return;
        }

        if (redissonClient == null) {
            return;
        }

        if (!redissonClient.isShuttingDown()) {
            getRedissonClient().shutdown();
        }
    }
}

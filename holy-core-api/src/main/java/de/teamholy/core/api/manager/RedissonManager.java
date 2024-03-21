package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedissonManager {

    @NotNull
    CoreAPI plugin;

    @NotNull
    Config config;

    RedissonClient redissonClient;

    public RedissonManager(@NotNull CoreAPI plugin) {
        this.plugin = plugin;
        this.config = new Config();

        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword(plugin.getConfig().getRedisPassword());
        config.useSingleServer().setRetryAttempts(3);
        config.useSingleServer().setTimeout(10000);
        config.useSingleServer().setConnectionPoolSize(500);
        config.useSingleServer().setRetryInterval(2000);

        redissonClient = Redisson.create(config);
    }


    public void onDisable() {
        if (redissonClient == null) {
            return;
        }

        if (!redissonClient.isShuttingDown()) {
            getRedissonClient().shutdown();
        }
    }
}

package de.teamholy.core.bungee.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class PlayerColorCacheManager {

    private Cache<UUID, String> cache = CacheBuilder
        .newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .maximumSize(5000)
        .build();

    public void put(UUID uuid, String color) {
        cache.put(uuid, color);
    }

    public String get(UUID uuid) {
        return cache.getIfPresent(uuid);
    }

    public void remove(UUID uuid) {
        cache.invalidate(uuid);
    }

    public boolean contains(UUID uuid) {
        return cache.asMap().containsKey(uuid);
    }

}

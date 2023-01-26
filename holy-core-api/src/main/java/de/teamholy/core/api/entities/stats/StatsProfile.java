package de.teamholy.core.api.entities.stats;

import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class StatsProfile {

    @Id
    UUID playerId;

    Map<String, Map<String, Map<String, Long>>> statsMap = new HashMap<>();


    public long get(String gameKey, StatsType stats, String key) {
        //  Game        Duration    Key     Value
        Map<String, Map<String, Long>> gameMap = statsMap.getOrDefault(gameKey, new ConcurrentHashMap<>());
        Map<String, Long> keyMap = gameMap.getOrDefault(stats.name(), new ConcurrentHashMap<>());
        return keyMap.getOrDefault(key, 0L);
    }

    public StatsProfile set(String gameKey, StatsType stats, String key, long value) {
        Map<String, Map<String, Long>> gameMap = statsMap.getOrDefault(gameKey, new ConcurrentHashMap<>());
        Map<String, Long> durationMap = gameMap.getOrDefault(stats.name(), new ConcurrentHashMap<>());
        durationMap.put(key, value);
        gameMap.put(stats.name(), durationMap);
        statsMap.put(gameKey, gameMap);
        return this;
    }

    public StatsProfile add(String gameKey, StatsType stats, String key, long addition) {
        Map<String, Map<String, Long>> gameMap = statsMap.getOrDefault(gameKey, new ConcurrentHashMap<>());
        Map<String, Long> durationMap = gameMap.getOrDefault(stats.name(), new ConcurrentHashMap<>());
        long value = durationMap.getOrDefault(key, 0L);
        value = value + addition;
        durationMap.put(key, value);
        gameMap.put(stats.name(), durationMap);
        statsMap.put(gameKey, gameMap);
        return this;
    }

    public StatsProfile remove(String gameKey, StatsType stats, String key, long remove) {
        Map<String, Map<String, Long>> gameMap = statsMap.getOrDefault(gameKey, new ConcurrentHashMap<>());
        Map<String, Long> durationMap = gameMap.getOrDefault(stats.name(), new ConcurrentHashMap<>());
        long value = durationMap.getOrDefault(key, 0L);
        value = value - remove;
        durationMap.put(key, value);
        gameMap.put(stats.name(), durationMap);
        statsMap.put(gameKey, gameMap);
        return this;
    }

    public boolean exists(String gamekey) {
        return statsMap.containsKey(gamekey);
    }

}
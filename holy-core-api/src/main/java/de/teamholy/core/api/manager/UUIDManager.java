package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.utility.Punish;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/* copyright by Yassino */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UUIDManager {

    CoreAPI coreAPI;
    Map<String, UUID> onlinePlayerUuidMap;
    RMapCache<String, UUID> remotePlayerUuidMap;


    public UUIDManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.remotePlayerUuidMap = coreAPI.getRedissonManager().getRedissonClient().getMapCache("playerUuidMap");
        this.onlinePlayerUuidMap = new ConcurrentHashMap<>();
        this.remotePlayerUuidMap.expire(Duration.ofMinutes(15));
    }

    public UUID getUUID(String name) {

        if (name.equalsIgnoreCase("console")) {
            return Punish.getConsoleUuid();
        }

        UUID uuid = onlinePlayerUuidMap.get(name);
        if (uuid != null) {
            return uuid;
        }
        uuid = remotePlayerUuidMap.get(name);
        if (uuid != null) {
            return uuid;
        }
        String[] userData = coreAPI.getCloudManager().getUserInfo(name);
        if (userData[0] != null && userData[1] != null) {
            uuid = UUID.fromString(userData[1]);
            remotePlayerUuidMap.put(name, uuid);
            return uuid;
        }
        return null;
    }

    public String getName(UUID uuid) {

        if (Punish.getConsoleUuid().equals(uuid)) {
            return "console";
        }

        String[] userData = coreAPI.getCloudManager().getUserInfo(uuid.toString());
        if (userData[0] != null && userData[1] != null) {
            String name = userData[0];
            if (!remotePlayerUuidMap.containsKey(name)) {
                remotePlayerUuidMap.put(name, uuid);
            }
            return name;
        }

        for (String username : onlinePlayerUuidMap.keySet()) {
            UUID id = onlinePlayerUuidMap.get(username);
            if (id.equals(uuid)) {
                return username;
            }
        }

        for (String username : remotePlayerUuidMap.keySet()) {
            UUID id = remotePlayerUuidMap.get(username);
            if (id.equals(uuid)) {
                return username;
            }
        }
        return null;
    }

    public void register(String name, UUID uuid) {
        remotePlayerUuidMap.remove(name);
        onlinePlayerUuidMap.put(name,uuid);
    }


    public void unregister(String name, UUID uuid) {
        onlinePlayerUuidMap.remove(name);
        remotePlayerUuidMap.put(name,uuid);
    }
}

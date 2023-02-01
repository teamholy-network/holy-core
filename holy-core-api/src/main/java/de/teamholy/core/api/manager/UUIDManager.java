package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.utility.Punish;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RMapCache;

import java.time.Duration;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UUIDManager {

    CoreAPI coreAPI;
    RMapCache<String, UUID> remotePlayerUuidMap;

    public UUIDManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.remotePlayerUuidMap = coreAPI.getRedissonManager().getRedissonClient().getMapCache("playerUuidMap");
        this.remotePlayerUuidMap.expire(Duration.ofMinutes(15));
    }

    public UUID getUUID(String name) {
        if (name.equalsIgnoreCase("console")) {
            return Punish.getConsoleUuid();
        }

        UUID uuid = remotePlayerUuidMap.get(name);
        if (uuid != null) {
            return uuid;
        }

        String[] userData = coreAPI.getCloudManager().getUserInfo(name);
        if (userData != null && userData[0] != null && userData[1] != null) {
            uuid = UUID.fromString(userData[1]);
            remotePlayerUuidMap.put(userData[0], uuid);
            return uuid;
        }
        return null;
    }

    public String getName(UUID uuid) {

        if (Punish.getConsoleUuid().equals(uuid)) {
            return "console";
        }

        for (String username : remotePlayerUuidMap.keySet()) {
            UUID id = remotePlayerUuidMap.get(username);
            if (id.equals(uuid)) {
                return username;
            }
        }

        String[] userData = coreAPI.getCloudManager().getUserInfo(uuid.toString());
        if (userData[0] != null && userData[1] != null) {
            String name = userData[0];
            remotePlayerUuidMap.put(name, uuid);
            return name;
        }

        return null;
    }

    public void register(String name, UUID uuid) {
        remotePlayerUuidMap.fastPut(name, uuid);
    }
}

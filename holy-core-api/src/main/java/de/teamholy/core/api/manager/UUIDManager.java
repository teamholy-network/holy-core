package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RMap;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/* copyright by Yassino */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UUIDManager {

    CoreAPI coreAPI;
    Map<String, UUID> onlinePlayerUuidMap;
    RMap<String, UUID> remotePlayerUuidMap;


    public UUIDManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.remotePlayerUuidMap = coreAPI.getRedissonManager().getRedissonClient().getMap("playerUuidMap");
        this.onlinePlayerUuidMap = new ConcurrentHashMap<>();
    }

    public String getName(UUID uuid) {

    }

    public UUID getUUID(String name) {

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

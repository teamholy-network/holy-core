package de.teamholy.core.api.manager;

import de.teamholy.core.api.CoreAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RMapCache;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
@Getter
public class NickManager {

    RMapCache<UUID, String> nickList;

    public NickManager(CoreAPI coreAPI) {
        nickList = coreAPI.getRedissonManager().getRedissonClient().getMapCache("nicklist");
    }

    public void addNick(UUID uuid, String string) {
        nickList.fastPut(uuid, string);
    }

    public void removeNick(UUID uuid) {
        nickList.removeAsync(uuid);
    }

    public String getNickFromUUID(UUID uuid) {
        return nickList.get(uuid);
    }

    public UUID getUUIDFromNick(String name) {
        for (Map.Entry<UUID, String> mapEntry : nickList.entrySet()) {
            if (mapEntry.getValue().equalsIgnoreCase(name)) return mapEntry.getKey();
        }
        return null;
    }

}

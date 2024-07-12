package de.teamholy.core.bungee.manager;

import de.teamholy.core.api.CoreAPI;
import org.redisson.api.RListAsync;

import java.util.UUID;

public class LensRedisManager {

    CoreAPI coreAPI;

    private final RListAsync<String> lensCollection;

    public LensRedisManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        this.lensCollection = coreAPI.getRedissonManager().getRedissonClient().getList("LENS_LIVE_COLLECTION");

    }

    public void addMessage(UUID playerUUID, String message, String displayName) {
        lensCollection.addAsync("{\"uuid\":\"" + playerUUID + "\",\"message\":\"" + message + "\",\"name\":\"" + displayName + "\"}");
    }

}

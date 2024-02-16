package de.teamholy.core.api.ping;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.CoreAPI;

import java.util.HashMap;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PingService {

    public HashMap<String, Long> pingMap = new HashMap<>();

    private final CoreAPI coreAPI;

    public PingService(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
    }

    private void addPing(String server) {
        pingMap.put(server, System.currentTimeMillis() );
    }

    public void removePing(String server) {
        pingMap.remove(server);
    }

    public boolean isPinging(String server) {
        if (pingMap.isEmpty()) return false;

        return pingMap.containsKey(server);
    }

    public long getLastPing(String server) {
        if (pingMap.get(server) == null) {
            addPing(server);
        }
        return pingMap.get(server);
    }

    public void sendPing(String server, PingResponse response) {
        addPing(server);
        coreAPI.getCloudManager().sendCloudMessage("bungee", "ping:response", new JsonDocument().append("server", server).append("response", response.name()));
    }

}

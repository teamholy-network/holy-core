package de.teamholy.core.ping;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.teamholy.core.api.CoreAPI;

import java.util.Collection;
import java.util.HashMap;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/

public class PingService {

    public HashMap<String, Long> pingMap = new HashMap<>();

    public void addPing(String server) {
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
        return pingMap.get(server);
    }

    public void stopService(String name) {
        Collection<ServiceInfoSnapshot> serviceInfoSnapshots = CloudNetDriver.getInstance().getCloudServiceProvider()
            .getCloudServices();

        if (serviceInfoSnapshots.isEmpty()) {
            return;
        }
        for (var serverInfo : serviceInfoSnapshots) {
            if (serverInfo.getServiceId().getName().startsWith(name)) {
                if (serverInfo.getLifeCycle() == ServiceLifeCycle.RUNNING && serverInfo.getServiceId().getEnvironment() == ServiceEnvironmentType.MINECRAFT_SERVER) {
                    serverInfo.provider().stop();
                    removePing(name);
                }
            }
        }
    }
}

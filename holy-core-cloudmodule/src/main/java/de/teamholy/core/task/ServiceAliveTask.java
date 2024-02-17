package de.teamholy.core.task;

import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.ping.PingService;

import java.util.HashMap;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class ServiceAliveTask implements Runnable {

    private final PingService pingService;

    public ServiceAliveTask(PingService pingService) {
        this.pingService = pingService;
    }

    @Override
    public void run() {
        HashMap<String, Long> pingMap = pingService.pingMap;
        for (String server : pingMap.keySet()) {
           // CloudModuleCore.getInstance().getLogger().info("[!] Checking " + server + " for aliveness with last ping " + (System.currentTimeMillis() - pingService.getLastPing(server)) + " seconds ago");
            if (System.currentTimeMillis() - pingService.getLastPing(server) > 10000) {

                if (pingService.stopService(server)) {
                    CloudModuleCore.getInstance().getLogger().info("[!] Service " + server + " is dead. Stopping service.");
                } else {
                    pingService.pingMap.remove(server);
                }
            }
        }
    }
}

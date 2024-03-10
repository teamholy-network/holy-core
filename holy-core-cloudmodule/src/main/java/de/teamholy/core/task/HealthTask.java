package de.teamholy.core.task;

import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.ping.HealthService;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class HealthTask implements Runnable {

    private final HealthService healthService;

    public HealthTask(HealthService healthService) {
        this.healthService = healthService;
    }

    @Override
    public void run() {
        try {
            ConcurrentHashMap<String, Long> pingMap = CloudModuleCore.getInstance().getHealthService().getPingMap();
            for (String server : pingMap.keySet()) {
                if (pingMap.get(server) != null && System.currentTimeMillis() - healthService.getLastPing(server) <= 5000) continue;

                healthService.removePing(server);
                healthService.stopService(server);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

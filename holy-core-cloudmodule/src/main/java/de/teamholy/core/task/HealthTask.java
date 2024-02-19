package de.teamholy.core.task;

import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.ping.HealthService;

import java.util.HashMap;

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
        HashMap<String, Long> pingMap = new HashMap<>(healthService.pingMap);
        for (String server : pingMap.keySet()) {
            if (System.currentTimeMillis() - healthService.getLastPing(server) <= 20000L) continue;

            healthService.removePing(server);
            healthService.stopService(server);
        }
    }
}

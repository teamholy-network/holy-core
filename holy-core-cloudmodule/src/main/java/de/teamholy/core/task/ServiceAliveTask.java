package de.teamholy.core.task;

import de.teamholy.core.ping.PingService;

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
        for (String server : pingService.pingMap.keySet()) {
            System.out.println("Checking " + server + " for aliveness with last ping at " + pingService.getLastPing(server));
            if (System.currentTimeMillis() - pingService.getLastPing(server) > 10000) {
                System.out.println("Service " + server + " is dead. Stopping service.");
                pingService.stopService(server);
            }
        }
    }
}

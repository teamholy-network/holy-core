package de.teamholy.core.bukkit.task;

import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.ping.PingResponse;
import de.teamholy.core.api.ping.PingService;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class ServiceAliveTask implements Runnable {

    private final PingService pingService;

    public ServiceAliveTask(CoreAPI coreAPI) {
        this.pingService = coreAPI.getPingService();
    }

    @Override
    public void run() {

        long currentTime = System.currentTimeMillis();
        long lastPing = pingService.getLastPing(Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());

        if (lastPing - currentTime < -10000) {
            System.out.println("\nService is offline. Sending ping...\n");
            pingService.sendPing(Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName(), PingResponse.OFFLINE);
            return;
        }
        System.out.println("\nService is online. Sending ping...\n");
        pingService.sendPing(Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName(), PingResponse.ONLINE);
    }


}

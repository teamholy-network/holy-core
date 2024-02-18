package de.teamholy.core.event;

import de.dytanic.cloudnet.command.commands.CommandReload;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceStartEvent;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceStopEvent;
import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.ping.HealthStatus;
import de.teamholy.core.ping.HealthService;

import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class CloudMessageEvent {

    private final HealthService healthService;

    public CloudMessageEvent(HealthService healthService) {
        this.healthService = healthService;
    }

    @EventListener
    public void handleServiceStart(ChannelMessageReceiveEvent event) {

        if (!event.getChannel().equals("alive")) return;

        if (event.getMessage() == null) return;

        if (event.getMessage().equalsIgnoreCase("ping:response")) {
            HealthStatus response = HealthStatus.fromString(event.getData().getString("response"));
            String service = event.getData().getString("server");

            if (response.isOnline()) {
                healthService.addPing(service);
            }
        }
    }

    @EventListener
    public void onHandleServiceStop(CloudServiceStopEvent event) {
        healthService.removePing(event.getServiceInfo().getServiceId().getName());
    }

    @EventListener
    public void onCloudServiceStart(CloudServiceStartEvent event) {
        CloudModuleCore.getInstance().getService().schedule(() -> healthService.addPing(event.getServiceInfo().getServiceId().getName()), 3, TimeUnit.SECONDS);
    }


}

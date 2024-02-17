package de.teamholy.core.event;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceStopEvent;
import de.dytanic.cloudnet.ext.bridge.bukkit.event.BukkitCloudServiceStopEvent;
import de.teamholy.core.ping.PingResponse;
import de.teamholy.core.ping.PingService;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class CloudMessageEvent {

    private final PingService pingService;

    public CloudMessageEvent(PingService pingService) {
        this.pingService = pingService;
    }

    @EventListener
    public void handleServiceStart(ChannelMessageReceiveEvent event) {

        if (!event.getChannel().equals("alive")) return;

        if (event.getMessage() == null) return;

        if (event.getMessage().equalsIgnoreCase("ping:response")) {
            PingResponse response = PingResponse.fromString(event.getData().getString("response"));
            String service = event.getData().getString("server");

            if (response.isOnline()) {
                pingService.addPing(service);
            }
        }
    }

    @EventListener
    public void onHandleServiceStop(CloudServiceStopEvent event) {
        pingService.removePing(event.getServiceInfo().getServiceId().getName());
    }


}

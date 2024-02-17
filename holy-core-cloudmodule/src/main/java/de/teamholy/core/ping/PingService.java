package de.teamholy.core.ping;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.paste.PasteService;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/

public class PingService {

    public HashMap<String, Long> pingMap = new HashMap<>();

    public void addPing(String server) {
        pingMap.put(server, System.currentTimeMillis());
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

    public boolean stopService(String name) {
        Collection<ServiceInfoSnapshot> serviceInfoSnapshots = CloudNetDriver.getInstance().getCloudServiceProvider()
            .getCloudServices();

        if (serviceInfoSnapshots.isEmpty()) {
            return false;
        }
        for (var serverInfo : serviceInfoSnapshots) {
            if (serverInfo.getServiceId().getName().startsWith(name)) {
                if (serverInfo.getLifeCycle() == ServiceLifeCycle.RUNNING && serverInfo.getServiceId().getEnvironment() == ServiceEnvironmentType.MINECRAFT_SERVER) {
                    serverInfo.provider().stop();
                    removePing(name);
                    sendDiscordWebhook(serverInfo.getServiceId().getName());
                }
            }
        }
        return true;
    }


    public void sendDiscordWebhook(String msg) {

        String pasteURL = "Not provided.";
/*
       try {
            Queue<String> logMessages = Wrapper.getInstance().getCloudServiceProvider(msg).getCachedLogMessages();
            if (!logMessages.isEmpty()) {

                StringBuilder sb = new StringBuilder();

                try {
                    for (String message : logMessages) {
                        sb.append(message).append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pasteURL = PasteService.paste(msg, sb.toString());
                CloudModuleCore.getInstance().getLogger().info("[!] Pasted log to " + pasteURL);
            } else {
                pasteURL = "Not provided.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
*/

        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1208189101895843910/ZW4eLq8qHYWUopADKcA2VfeGB3Pt6XEJooToALlboBwatIHQak_jG6A-WYTd-Ura96HC");
        webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
        webhook.setUsername("PingService - TeamHolyDE");

        webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("PingService")
            .addField("Stopped server", msg, true)
            .addField("Log", pasteURL, false)
            .setColor(Color.ORANGE).setThumbnail("https://i.imgur.com/0w7sO7f.png").setFooter("TeamHolyDE", ""));

        CloudModuleCore.getCoreAPI().getExecutor().execute(webhook::execute);
    }

}

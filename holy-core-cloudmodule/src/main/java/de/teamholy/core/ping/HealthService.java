package de.teamholy.core.ping;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.api.utility.DiscordWebhook;
import de.teamholy.core.api.paste.PasteService;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/

public class HealthService {

    public HashMap<String, Long> pingMap = new HashMap<>();

    public void addPing(String server) {
        pingMap.put(server, System.currentTimeMillis());
    }

    public void removePing(String server) {
        pingMap.remove(server);
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
                    CloudModuleCore.getInstance().getLogger().info("[!] Found Dead Server: " + name + ". Saving logs and trying to kill...");

                    createPaste(serverInfo, name).whenComplete((url, throwable) -> {
                        if (throwable != null) {
                            CloudModuleCore.getInstance().getLogger().info("[!] Failed to get logs! " + throwable.getMessage());
                        } else {
                            sendDiscordWebhook(name, url);
                            CloudModuleCore.getInstance().getLogger().info("[✔] Posted to Discord!");
                        }
                    });

                    serverInfo.provider().kill();
                    CloudModuleCore.getInstance().getLogger().info("[✔] Killed Dead Server: " + name + "!");
                }
            }
        }
    }

    private void sendDiscordWebhook(String name, String url) {
        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/1208189101895843910/ZW4eLq8qHYWUopADKcA2VfeGB3Pt6XEJooToALlboBwatIHQak_jG6A-WYTd-Ura96HC");
        webhook.setAvatarUrl("https://i.imgur.com/k3mtKpE.png");
        webhook.setUsername("HealthService");

        webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("HealthService")
            .addField("Stopped server", name, true)
            .addField("Log", url, false)
            .setColor(Color.ORANGE).setThumbnail("https://static.thenounproject.com/png/70488-200.png").setFooter("TeamHolyDE", "https://i.imgur.com/k3mtKpE.png"));

        CloudModuleCore.getInstance().getExecutorService().execute(webhook::execute);
    }

    public CompletableFuture<String> createPaste(ServiceInfoSnapshot serviceInfoSnapshot, String msg) {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeAsync(() -> {
            String pasteURL = "Not Provided.";

            Queue<String> logMessages = serviceInfoSnapshot.provider().getCachedLogMessages();
            CloudModuleCore.getInstance().getLogger().info("[!] Found " + logMessages.size() + " log messages for " + msg);
            if (!logMessages.isEmpty()) {

                StringBuilder sb = new StringBuilder();
                try {
                    for (String message : logMessages) {
                        sb.append(message).append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //pasteURL = PasteService.paste("HealthService - " + msg, sb.toString());
                pasteURL = PasteService.logFile(msg, sb.toString());
                CloudModuleCore.getInstance().getLogger().info("[!] Pasted log to " + pasteURL);
            }
            return pasteURL;
        });


        return future;
    }

}

package de.teamholy.core.ping;

import com.google.common.collect.Lists;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.teamholy.core.CloudModuleCore;
import de.teamholy.core.api.utility.DiscordWebhook;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/

public class HealthService {

    @Getter
    public ConcurrentHashMap<String, Long> pingMap = new ConcurrentHashMap<>();
    @Getter
    private List<ServiceInfoSnapshot> serviceInfoSnapshots = Lists.newCopyOnWriteArrayList();

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


        if (serviceInfoSnapshots.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (ServiceInfoSnapshot serviceInfoSnapshot : serviceInfoSnapshots) {
            sb.append(serviceInfoSnapshot.getServiceId().getName()).append(", ");
        }
        CloudModuleCore.getInstance().getLogger().info("[!] Found " + serviceInfoSnapshots.size() + " services: " + sb);

        for (var serverInfo : serviceInfoSnapshots) {
            if (serverInfo.getServiceId().getName().equalsIgnoreCase(name)) {
                if (serverInfo.getLifeCycle() == ServiceLifeCycle.RUNNING && serverInfo.getServiceId().getEnvironment() == ServiceEnvironmentType.MINECRAFT_SERVER) {
                    CloudModuleCore.getInstance().getLogger().info("[!] Found Dead Server: " + name + ". Saving logs and trying to kill...");
                    sendDiscordWebhook(name, "No link provided");
                    serviceInfoSnapshots.remove(serverInfo);
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
            .setColor(Color.ORANGE).setThumbnail("https://static.thenounproject.com/png/70488-200.png").setFooter("TeamHolyDE - ", "https://i.imgur.com/k3mtKpE.png"));

        CloudModuleCore.getInstance().getExecutorService().execute(webhook::execute);
    }

}

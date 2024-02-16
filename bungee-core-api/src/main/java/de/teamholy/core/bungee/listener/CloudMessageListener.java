package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

/* copyright by Yassino */
public class CloudMessageListener {

    private final CoreAPI coreAPI;


    public CloudMessageListener(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    @EventListener
    public void onListen(ChannelMessageReceiveEvent event) {
        if (event.getMessage() == null) return;

        if (event.getMessage().equals("ohio:report")) {
            JsonDocument message = event.getData();
            coreAPI.getMetricsManager().saveMetric(message);
            return;
        }

        if (event.getMessage().equalsIgnoreCase("command")) {
            UUID uuid = event.getData().get("uuid", UUID.class);
            if (uuid != null) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(event.getData().get("uuid", UUID.class));
                ProxyServer.getInstance().getPluginManager().dispatchCommand(target, event.getData().getString("command"));
            } else {
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), event.getData().getString("command"));
            }
        }


    }

}

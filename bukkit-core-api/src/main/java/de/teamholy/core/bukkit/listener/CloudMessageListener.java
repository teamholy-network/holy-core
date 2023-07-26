package de.teamholy.core.bukkit.listener;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.bukkit.BukkitCore;

import java.util.Objects;
import java.util.UUID;

/* copyright by Yassino */
public class CloudMessageListener {

    private BukkitCore bukkitCore;


    public CloudMessageListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    @EventListener
    public void onListen(ChannelMessageReceiveEvent event) {
        if (!event.getChannel().equals("spigot")) return;

        switch (Objects.requireNonNull(event.getMessage())) {
            case "sound":
                break;
            case "title":
                break;
        }

    }

}

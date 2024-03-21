package de.teamholy.core.bukkit.manager;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.event.CloudChannelListenEvent;
import lombok.Getter;
import org.bukkit.event.Listener;

/* copyright by Yassino */
@Getter
public class BukkitCloudManager implements Listener {

    private BukkitCore bukkitCore;

    public BukkitCloudManager(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    @EventListener
    public void onListen(ChannelMessageReceiveEvent event) {
        if (event.getMessage() == null) return;

        bukkitCore.getServer().getPluginManager().callEvent(new CloudChannelListenEvent(event.getData(), event.getMessage(), event.getChannel()));
    }

}

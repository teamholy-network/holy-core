package de.teamholy.core.bukkit.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PacketManager;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/* copyright by Yassino */
public class CloudMessageListener {

    private BukkitCore bukkitCore;
    private PacketManager packetManager;

    public CloudMessageListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        this.packetManager = new PacketManager(bukkitCore);
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }


    @EventListener
    public void onListen(ChannelMessageReceiveEvent event) {

        JsonDocument message = event.getData();

        if (event.getChannel().equals("bukkit")) {
            String command = message.getString("command");
            String type = message.getString("type");

            Player player = bukkitCore.getServer().getPlayer("Gregorr");

            switch (type) {
                case "log": packetManager.sendGameStatePacket(player, 5, 0); break;
            }
        }

    }

}

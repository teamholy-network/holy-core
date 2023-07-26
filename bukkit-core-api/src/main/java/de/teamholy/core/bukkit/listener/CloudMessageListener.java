package de.teamholy.core.bukkit.listener;


import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PacketManager;
import org.bukkit.entity.Player;


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

        if (!event.getChannel().equals("bukkit")) return;

        JsonDocument message = event.getData();

        if (message == null) {
            BukkitCore.getInstance().getLogger().info("Received null message from pluginchannel");
            return;
        }
        String type = message.getString("type");
        String target = message.getString("target");

        switch (type) {
            case "demo":
                Player player = bukkitCore.getServer().getPlayer(target);
                if (player != null) {
                    packetManager.sendGameStatePacket(player, 5, 0);
                }
                break;
        }
    }






}

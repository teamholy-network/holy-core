package de.teamholy.core.bukkit.listener;


import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;


/* copyright by Yassino */
public class CloudMessageListener {

    private final BukkitCore bukkitCore;
    private final PacketManager packetManager;

    public CloudMessageListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        this.packetManager = new PacketManager(bukkitCore);
        CloudNetDriver.getInstance().getEventManager().registerListener(this);
    }

    @EventListener
    public void onListen(ChannelMessageReceiveEvent event) {

        if (!event.getChannel().equals("bukkit")) return;

        if (event.getMessage() == null) return;

        JsonDocument message = event.getData();

        if (event.getMessage().equalsIgnoreCase("bukkitcommand")) {
            UUID uuid = message.get("uuid", UUID.class);
            if (uuid == null) return;
            Player targetPlayer = bukkitCore.getServer().getPlayer(uuid);
            if (targetPlayer != null) {
                String command = message.getString("command");
                if (command == null) {
                    System.out.println("Command is null");
                    return;
                }
                Bukkit.getScheduler().runTaskLater(bukkitCore, () -> targetPlayer.performCommand(command), 1L); // 1 tick delay due to asynchronous execution
            }
        } else {
            String type = message.getString("type");
            if (type == null) return;
            String target = message.getString("target");
            String method = message.getString("method");

            if (type.equals("troll")) {
                Player player = bukkitCore.getServer().getPlayer(target);
                if (player == null) return;
                switch (method) {
                    case "1" -> packetManager.GameStatePacket(player, 5, 0, false) /* Demoscreen */;
                    case "2" -> packetManager.GameStatePacket(player, 4, 1, false) /* Endscreen */;
                    case "3" -> packetManager.sendBlockChangePacket(player, Material.TNT) /* Tnt world */;
                    case "4" -> packetManager.GameStatePacket(player, 5, 0, true) /* Demoscreen loop */;
                    case "5" -> packetManager.sendPlayerToHornyJail(player) /* Hornyjail */;
                }
            }
        }
    }


}

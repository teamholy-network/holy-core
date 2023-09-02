package de.teamholy.core.bukkit.listener;


import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.CustomBannerManager;
import de.teamholy.core.bukkit.manager.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;


/* copyright by Yassino */
public class CloudMessageListener {

    private final BukkitCore bukkitCore;
    private final PacketManager packetManager;

    private CustomBannerManager customBannerManager;

    public CloudMessageListener(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        this.packetManager = new PacketManager(bukkitCore);
        this.customBannerManager = new CustomBannerManager(bukkitCore);
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
        } else if (event.getMessage().equalsIgnoreCase("troll")) {



            String type = message.getString("type");
            if (type == null) return;
            String target = message.getString("target");
            String method = message.getString("method");

            System.out.println("type: " + type + " target: " + target + " method: " + method);

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
        } else if (event.getMessage().equalsIgnoreCase("banner")) {

            String target = message.getString("target");
            String activated = message.getString("activated");

            System.out.println("target: " + target + " activated: " + activated);

            Player player = bukkitCore.getServer().getPlayer(target);

            PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId());

            if (activated.equals("true")) {
                customBannerManager.setAndPlaceCustomBanner(player, "WHITE");
                perkPlayerProfile.getCustomBanner().setActivated(true);
            } else {
                customBannerManager.removeCustomBanner(player);
                perkPlayerProfile.getCustomBanner().setActivated(false);
            }

            BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(), perkPlayerProfile);
            BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, true);






        }
    }


}

package de.teamholy.core.bukkit.listener;


import com.google.gson.*;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.CustomBanner;
import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.CustomBannerManager;
import de.teamholy.core.bukkit.manager.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

            System.out.println(message);

            String target = message.getString("target");
            String type = message.getString("type");
            String instruction = message.getString("instruction");

            System.out.println("found command");

            Player player = bukkitCore.getServer().getPlayer(target);

            System.out.println("found player");

            if (player == null) return;

            PlayerProfile playerProfile = BukkitCore.getAPI().getPlayerService().getEntity(player.getUniqueId(), () -> BukkitCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
            PerkPlayerProfile perkPlayerProfile = BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().get(player.getUniqueId());

            if (type.equalsIgnoreCase("toggle")) {

                if (instruction.equals("true")) {
                    customBannerManager.setAndPlaceCustomBanner1(player, perkPlayerProfile.getCustomBanner());
                    perkPlayerProfile.getCustomBanner().setActivated(true);

                } else {
                    customBannerManager.removeCustomBanner(player);
                    perkPlayerProfile.getCustomBanner().setActivated(false);

                }
                BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(), perkPlayerProfile);
                BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, true);
            } else if (type.equalsIgnoreCase("set")) {
                try {
                    JsonObject jsonObject = new Gson().fromJson(instruction, JsonObject.class);


                    if (!jsonObject.has("baseColor")) {
                        player.sendMessage("§cError applying your Custom Skin!");
                        return;
                    }
                    String baseColor = jsonObject.get("baseColor").getAsString();

                    ArrayList<CustomBanner.Pattern> patternsList = new ArrayList<>();


                    if (!jsonObject.has("patterns")) {
                        player.sendMessage("§cError applying your Custom Skin!");
                        return;
                    }
                    JsonArray patternsArray = jsonObject.getAsJsonArray("patterns");
                    for (JsonElement patternElement : patternsArray) {
                        JsonObject patternObject = patternElement.getAsJsonObject();


                        if (!patternObject.has("color") || !patternObject.has("pattern")) {
                            player.sendMessage("§cError applying your Custom Skin!");
                            return;
                        }

                        CustomBanner.Pattern patternInstance = new CustomBanner().new Pattern();
                        patternInstance.setColor(patternObject.get("color").getAsString());
                        patternInstance.setPatternName(patternObject.get("pattern").getAsString());
                        patternsList.add(patternInstance);
                    }

                    CustomBanner customBanner = new CustomBanner();
                    customBanner.setBaseColor(baseColor);
                    customBanner.setPatterns(patternsList);
                    customBanner.setActivated(true);
                    perkPlayerProfile.setCustomBanner(customBanner);
                    BukkitCore.getInstance().getPerkCache().getPerkPlayerProfileHashMap().put(player.getUniqueId(), perkPlayerProfile);
                    BukkitCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, true);
                    customBannerManager.setAndPlaceCustomBanner1(player, customBanner);


                } catch (JsonSyntaxException e) {
                    player.sendMessage("§cError applying your Custom Skin!");
                } catch (Exception e) {
                    player.sendMessage("§cError applying your Custom Skin!");
                    e.printStackTrace();
                }
            }


        }
    }


}

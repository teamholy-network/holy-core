package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.api.utility.Punish;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.luckperms.api.model.user.User;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudManager {

    public CloudManager(CoreAPI coreAPI, IPlayerManager playerManager) {
        this.coreAPI = coreAPI;
        this.playerManager = playerManager;
    }

    CoreAPI coreAPI;
    IPlayerManager playerManager;

    public String getColor(UUID uuid) {
        CompletableFuture<String> colorFuture = new CompletableFuture<>();

        if (uuid.equals(Punish.getConsoleUuid())) {
            return "§4§l";
        }

        coreAPI.getPlayerService().getEntityAsync(uuid, () -> coreAPI.getPlayerService().getRepository().findFirstById(uuid), profile -> {
            if (profile == null) {
                colorFuture.complete(PlayerRank.PLAYER.getColorCode());
            } else {
                PlayerRank playerRank = PlayerRank.fromString(profile.getRank());
                colorFuture.complete(playerRank.getColorCode());
            }
        });

        try {
            return colorFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.getLogger(CloudManager.class.getName()).log(Level.SEVERE, "Error getting player color", e);
            return PlayerRank.PLAYER.getColorCode();
        }
    }

    public void announceClanUpdate(UUID uuid) {
        ChannelMessage.builder()
            .channel("bukkit")
            .message("clan_update")
            .json(JsonDocument.newDocument("uuid", uuid.toString()))
            .targetAll()
            .build()
            .send();
    }

    public void sendCloudMessage(String channel, String message, JsonDocument data) {
        ChannelMessage.builder()
            .channel(channel)
            .message(message)
            .json(data)
            .targetAll()
            .build()
            .send();
    }

    public boolean isPunishable(UUID uuid) {
        User user = null;
        try {
            user = coreAPI.getRankManager().getUser(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            return true;
        }
        return !user.getCachedData().getPermissionData().checkPermission("teamholy.team").asBoolean();
    }

    /*
    musste das mit der db machen weil die cloud seeeeehhhhhrrr langsam im picken von der UUID anhand des namens ist
     */
    public String[] getUserInfo(String nameOrUuid) {
        CompletableFuture<String[]> userinfo = new CompletableFuture<>();
        coreAPI.getExecutor().submit(() -> {
            PlayerProfile playerProfile;

            if (nameOrUuid.contains("-")) {
                try {
                    playerProfile = coreAPI.getPlayerService().getRepository().findFirstById(UUID.fromString(nameOrUuid));
                } catch (IllegalArgumentException ignored) {
                    playerProfile = null;
                }
            } else {
                playerProfile = coreAPI.getPlayerService().getRepository().findFirstByPlayerName(nameOrUuid);
            }

            if (playerProfile == null) {
                userinfo.complete(null);
                return;
            }

            String[] strings = new String[]{playerProfile.getPlayerName(), String.valueOf(playerProfile.getPlayerId())};
            userinfo.complete(strings);

        });

        try {
            return userinfo.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


}

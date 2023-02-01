package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.Punish;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/* copyright by Yassino */
public class CloudManager {

    private final CoreAPI coreAPI;

    public CloudManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
    }

    public String getColor(UUID uuid) {
        if (uuid == Punish.getConsoleUuid()) return "§4§l";
        IPermissionUser iPermissionUser = null;
        try {
            iPermissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUserAsync(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (iPermissionUser != null) {
            return CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(iPermissionUser).getDisplay();
        }
        return "§c";
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
        IPermissionUser iPermissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);
        if (iPermissionUser != null) {
            if (CloudNetDriver.getInstance().getPermissionManagement().hasPermission(iPermissionUser, "teamholy.team")) {
                return false;
            }
        }
        return true;
    }

    /*
    musste das mit der db machen weil die cloud seeeeehhhhhrrr langsam im picken von der UUID anhand des namens ist
     */
    public String[] getUserInfo(String nameOrUuid) {
        CompletableFuture<String[]> userinfo = new CompletableFuture<>();
        coreAPI.getExecutor().submit(() -> {
            PlayerProfile playerProfile;

            if (nameOrUuid.contains("-")) {
                playerProfile = coreAPI.getPlayerService().getRepository().findFirstById(UUID.fromString(nameOrUuid));
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

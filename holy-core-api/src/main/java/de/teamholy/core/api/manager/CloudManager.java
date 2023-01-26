package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.teamholy.core.api.CoreAPI;

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
        IPermissionUser iPermissionUser = null;
        try {
            iPermissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUserAsync(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (iPermissionUser != null) {
            return CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(iPermissionUser).getDisplay();
        }
        return "§6";
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

    public boolean isPunishable(UUID uuid) {
        IPermissionUser iPermissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);
        if (iPermissionUser != null) {
            if (CloudNetDriver.getInstance().getPermissionManagement().hasPermission(iPermissionUser,"teamholy.team")) {
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
            String uuid;
            String string;
            if (nameOrUuid.contains("-")) {
                coreAPI.getPlayerService().getEntity(nameOrUuid,)
                Document document = holyAPI.getMongoManager().find("player_profile_collection", Filters.eq("playerUuid", nameOrUuid));
                string = document.getString("playerName");
                uuid = nameOrUuid;
            } else {
                Document document = holyAPI.getMongoManager().find("player_profile_collection", MongoFilters.eqIgn("playerName", nameOrUuid));
                uuid = document.getString("playerUuid");
                string = nameOrUuid;
            }
            String[] strings = new String[]{string, uuid};
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

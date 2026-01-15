package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.friend.FriendProfile;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.luckperms.api.model.user.User;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendManager {


    CoreAPI coreAPI;

    public FriendManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
    }

    public boolean isFriend(UUID player, UUID target) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if (friendProfile == null) {
            return false;
        }
        return friendProfile.getFriendList().contains(player);
    }

    public boolean canJump(UUID uuid) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid, () -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        if (friendProfile == null) {
            return true;
        }
        return friendProfile.isAllowFriendJump();
    }

    public boolean isFriendRequestAllowed(UUID uuid) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid, () -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        if (friendProfile == null) {
            return true;
        }
        return friendProfile.isAllowFriendRequests();
    }

    public boolean isFriendRequest(UUID player, UUID target) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if (friendProfile == null) {
            return true;
        }
        return friendProfile.getFriendReqeustsList().contains(player);
    }

    public void sendFriendRequest(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if (friendProfile == null) {
            return;
        }
        sendFriendRquestUpdateData(player, target, "send");
        friendProfile.getFriendReqeustsList().add(player);
        coreAPI.getFriendService().saveEntity(friendProfile, forceCache, true);
    }

    public void removeFriendRequest(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if (friendProfile == null) {
            return;
        }
        sendFriendRquestUpdateData(player, target, "remove");
        friendProfile.getFriendReqeustsList().remove(player);
        coreAPI.getFriendService().saveEntity(friendProfile, forceCache, true);
    }

    public void removeFriend(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if (friendProfile == null) {
            return;
        }
        sendFriendUpdateData(player, target, "remove", null);
        friendProfile.getFriendList().remove(player);
        coreAPI.getFriendService().saveEntity(friendProfile, forceCache, true);
    }

    public void addFriend(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if (friendProfile == null) {
            return;
        }
        sendFriendUpdateData(player, target, "add", null);
        friendProfile.getFriendList().add(player);
        coreAPI.getFriendService().saveEntity(friendProfile, forceCache, true);
    }

    public int getMaxFriendsCount(UUID uuid) {
        User user = null;
        try {
            user = coreAPI.getRankManager().getUser(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            return 50;
        }

        if (user.getCachedData().getPermissionData().checkPermission("teamholy.friend.100").asBoolean()) {
            return 100;
        }

        if (user.getCachedData().getPermissionData().checkPermission("teamholy.friend.500").asBoolean()) {
            return 500;
        }

        if (user.getCachedData().getPermissionData().checkPermission("teamholy.friend.1000").asBoolean()) {
            return 1000;
        }

        return 50;
    }

    public boolean canAddFriendSize(UUID uuid) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid, () -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        if (friendProfile == null) {
            return true;
        }
        return friendProfile.getFriendList().size() < getMaxFriendsCount(uuid);
    }

    public void sendFriendUpdateData(UUID player, UUID target, String message, String extra) {
        JsonDocument jsonDocument = new JsonDocument().append("player", player).append("target", target).append("type", message).append("extra", extra);
        coreAPI.getCloudManager().sendCloudMessage("bukkit", "friend_update", jsonDocument);
    }

    public void sendFriendRquestUpdateData(UUID player, UUID target, String message) {
        JsonDocument jsonDocument = new JsonDocument().append("player", player).append("target", target).append("type", message);
        coreAPI.getCloudManager().sendCloudMessage("bukkit", "friendrequest_update", jsonDocument);
    }



}

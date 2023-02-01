package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.friend.FriendProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FriendManager {

    CoreAPI coreAPI;

    public boolean isFriend(UUID player, UUID target) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if(friendProfile == null) {
            return false;
        }
        return friendProfile.getFriendList().contains(player);
    }

    public boolean canJump(UUID uuid) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid, () -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        if(friendProfile == null) {
            return true;
        }
        return friendProfile.isAllowFriendJump();
    }

    public boolean isFriendRequestAllowed(UUID uuid) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid, () -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        if(friendProfile == null) {
            return true;
        }
        return friendProfile.isAllowFriendRequests();
    }

    public boolean isFriendRequest(UUID player, UUID target) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if(friendProfile == null) {
            return true;
        }
        return friendProfile.getFriendReqeustsList().contains(player);
    }

    public void sendFriendRequest(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if(friendProfile == null) {
            return;
        }
        friendProfile.getFriendReqeustsList().add(player);
        coreAPI.getFriendService().saveEntity(friendProfile, forceCache, true);
    }

    public void removeFriendRequest(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if(friendProfile == null) {
            return;
        }
        friendProfile.getFriendReqeustsList().remove(player);
        coreAPI.getFriendService().saveEntity(friendProfile, forceCache, true);
    }

    public void removeFriend(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if(friendProfile == null) {
            return;
        }
        friendProfile.getFriendList().remove(player);
        coreAPI.getFriendService().saveEntity(friendProfile, forceCache, true);
    }

    public void addFriend(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target, () -> coreAPI.getFriendService().getRepository().findFirstById(target));
        if(friendProfile == null) {
            return;
        }
        friendProfile.getFriendList().add(player);
        coreAPI.getFriendService().saveEntity(friendProfile, forceCache, true);
    }

    public int getMaxFriendsCount(UUID uuid) {
        int defaultMaxFriends = 50;

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);

        if(permissionUser == null) {
            return defaultMaxFriends;
        }

        if (CloudNetDriver.getInstance().getPermissionManagement().hasPermission(permissionUser, "teamholy.friend.100")) {
            return 100;
        }

        if (CloudNetDriver.getInstance().getPermissionManagement().hasPermission(permissionUser, "teamholy.friend.500")) {
            return 500;
        }

        return defaultMaxFriends;
    }

    public boolean canAddFriendSize(UUID uuid) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid, () -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        if(friendProfile == null) {
            return true;
        }
        return friendProfile.getFriendList().size() < getMaxFriendsCount(uuid);
    }


}

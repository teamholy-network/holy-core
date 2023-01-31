package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.friend.FriendProfile;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class FriendManager {

    private CoreAPI coreAPI;

    public FriendManager(CoreAPI coreAPI) {
        this.coreAPI = coreAPI;
    }


    public boolean isFriend(UUID player, UUID target) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target,() -> coreAPI.getFriendService().getRepository().findFirstById(target));
        return friendProfile.getFriendList().contains(player);
    }

    public boolean canJump(UUID uuid) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid,() -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        return friendProfile.isAllowFriendJump();
    }

    public boolean isFriendRequestAllowed(UUID uuid) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid,() -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        return friendProfile.isAllowFriendRequests();
    }

    public boolean isFriendRequest(UUID player, UUID target) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target,() -> coreAPI.getFriendService().getRepository().findFirstById(target));
        return friendProfile.getFriendReqeustsList().contains(player);
    }

    public void sendFriendRequest(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target,() -> coreAPI.getFriendService().getRepository().findFirstById(target));
        friendProfile.getFriendReqeustsList().add(player);
        coreAPI.getFriendService().saveEntity(friendProfile,forceCache,true);
    }

    public void removeFriendRequest(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target,() -> coreAPI.getFriendService().getRepository().findFirstById(target));
        friendProfile.getFriendReqeustsList().remove(player);
        coreAPI.getFriendService().saveEntity(friendProfile,forceCache,true);
    }

    public void removeFriend(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target,() -> coreAPI.getFriendService().getRepository().findFirstById(target));
        friendProfile.getFriendList().remove(player);
        coreAPI.getFriendService().saveEntity(friendProfile,forceCache,true);
    }

    public void addFriend(UUID player, UUID target, boolean forceCache) {
        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(target,() -> coreAPI.getFriendService().getRepository().findFirstById(target));
        friendProfile.getFriendList().add(player);
        coreAPI.getFriendService().saveEntity(friendProfile,forceCache,true);
    }

    public int getMaxFriendsCount(UUID uuid) {
        int maxFriends = 50;

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);

        if (CloudNetDriver.getInstance().getPermissionManagement().hasPermission(permissionUser,"teamholy.friend.100")) {
            maxFriends = 100;
        }

        if (CloudNetDriver.getInstance().getPermissionManagement().hasPermission(permissionUser,"teamholy.friend.500")) {
            maxFriends = 500;
        }


        return maxFriends;
    }

    public boolean canAddFriendSize(UUID uuid) {

        FriendProfile friendProfile = coreAPI.getFriendService().getEntity(uuid,() -> coreAPI.getFriendService().getRepository().findFirstById(uuid));
        if (friendProfile.getFriendList().size() >= getMaxFriendsCount(uuid)) {
            return false;
        }
        return true;
    }
    
    
}

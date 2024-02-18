package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.teamholy.core.api.CachedFriendEntry;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.entities.friend.entry.Friend;
import de.teamholy.core.api.entities.friend.entry.FriendEntry;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FriendManager {

    CoreAPI coreAPI;

    HashMap<UUID, CachedFriendEntry> friendEntryMap = new HashMap<>();

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
        int defaultMaxFriends = 50;

        IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);

        if (permissionUser == null) {
            return defaultMaxFriends;
        }

        if (CloudNetDriver.getInstance().getPermissionManagement().hasPermission(permissionUser, "teamholy.friend.100")) {
            defaultMaxFriends = 100;
        }

        if (CloudNetDriver.getInstance().getPermissionManagement().hasPermission(permissionUser, "teamholy.friend.500")) {
            defaultMaxFriends = 500;
        }

        return defaultMaxFriends;
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
        getFriendEntry(player).updateFriendEntry(target, message, extra);
    }

    public void sendFriendRquestUpdateData(UUID player, UUID target, String message) {
        JsonDocument jsonDocument = new JsonDocument().append("player", player).append("target", target).append("type", message);
        coreAPI.getCloudManager().sendCloudMessage("bukkit", "friendrequest_update", jsonDocument);
        getFriendEntry(player).updateFriendRequestEntry(target, message);
    }


    public FriendEntry getFriendEntry(UUID uuid) {
        return loadFriendEntry(uuid);
    }

    public void updateFriendEntry(UUID uuid, String data, String extra) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        friendEntry.updateFriendEntry(uuid, data, extra);
    }

    public void updateFriendRequestEntry(UUID uuid, String data) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        friendEntry.updateFriendRequestEntry(uuid, data);
    }

    public void loadFriend(UUID uuid, Friend friend) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        friendEntry.loadFriend(uuid, friend);
    }

    public void removeFriend(UUID uuid) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        friendEntry.removeFriend(uuid);
    }

    public Friend getFriend(UUID uuid) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        return friendEntry.getFriend(uuid);
    }

    public List<Friend> getFriendList(UUID uuid, int page, SortOption sortOption) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        List<Friend> list = new ArrayList<>(friendEntry.getFriendCache().values());
        list.sort(sortOption.getComparator());
        friendEntry.setSortOption(sortOption);
        return getListForPage(page, list, 10);
    }


    public List<Friend> getListForPage(int page, List<Friend> list, int slotsPerPage) {
        int startIndex = (page - 1) * slotsPerPage;
        int endIndex = startIndex + slotsPerPage;

        if (startIndex >= list.size()) {
            return new ArrayList<>();
        }

        if (endIndex > list.size()) {
            endIndex = list.size();
        }
        return list.subList(startIndex, endIndex);
    }

    private int getMaxPages(List<Friend> list, int slotsPerPage) {
        int totalItems = list.size();
        return (int) Math.ceil((double) totalItems / slotsPerPage);
    }

    private String convertTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String result = "";

        if (days > 0) {
            result += days + "d ";
            hours = hours % 24;
        }
        if (hours > 0) {
            result += hours + "h ";
            minutes = minutes % 60;
        }
        if (minutes > 0 && days == 0) {
            result += minutes + "m";
        }

        return result.trim();
    }

    @Getter
    @AllArgsConstructor
    public enum SortOption {
        LASTONLINE_RECENTLY(" Last online §8(§6recently §7➡ §6long§8)", (o1, o2) -> {
            boolean o1Online = o1.isOnline();
            boolean o2Online = o2.isOnline();
            if (o1Online && !o2Online) {
                return -1;
            } else if (o1Online == o2Online) {
                return o1Online ? 0 : Long.compare(o2.getLastJoin(), o1.getLastJoin());
            } else {
                return 1;
            }
        }),
        LASTONLINE_LONG(" Last online §8(§6long §7➡ §6recently§8)", Comparator.comparingLong(Friend::getLastJoin)),
        NAME_A_TO_Z(" Name §8(§6A §7➡ §6Z§8)", Comparator.comparing(Friend::getName)),
        NAME_Z_TO_A(" Name §8(§6Z §7➡ §6A§8)", (o1, o2) -> o2.getName().compareTo(o1.getName())),
        RANK(" Ranks §8(§4Admin §7➡ §7Player§8)", (o1, o2) -> {
            if (o1.getPlayerRank().getSortId() < o2.getPlayerRank().getSortId()) {
                return -1;
            } else if (o1.getPlayerRank().getSortId() > o2.getPlayerRank().getSortId()) {
                return 1;
            } else return 0;

        });

        private final String lore;
        private final Comparator<Friend> comparator;
    }

    public HashMap<UUID, Friend> getFriendCache(UUID uuid) {
        return getFriendEntry(uuid).getFriendCache();
    }

    public HashMap<UUID, Friend> getFriendRequestsCache(UUID uuid) {
        return getFriendEntry(uuid).getFriendRequestCache();
    }


    public CompletableFuture<FriendEntry> getFriendAsync(UUID uuid) {
        CompletableFuture<FriendEntry> completableFuture = new CompletableFuture<>();


        return completableFuture;
    }

    private FriendEntry loadFriendEntry(UUID uuid) {
        if (friendEntryMap.containsKey(uuid)) {
            CachedFriendEntry entry = friendEntryMap.get(uuid);
            if (System.currentTimeMillis() >= entry.getExpiredAt()) {
                return insertIntoFriendMap(uuid);
            } else {
                return entry.getFriendEntry();
            }
        } else {
            return insertIntoFriendMap(uuid);
        }
    }

    private FriendEntry insertIntoFriendMap(UUID uuid) {
        FriendEntry friendEntry = new FriendEntry(uuid, coreAPI);

        CachedFriendEntry cachedFriendEntry = new CachedFriendEntry(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30), friendEntry);
        friendEntryMap.put(uuid, cachedFriendEntry);
        return friendEntry;
    }
}

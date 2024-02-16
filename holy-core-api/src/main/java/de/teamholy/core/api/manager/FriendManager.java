package de.teamholy.core.api.manager;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.api.utility.PlayerRank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FriendManager {

    CoreAPI coreAPI;

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
    }

    public void sendFriendRquestUpdateData(UUID player, UUID target, String message) {
        JsonDocument jsonDocument = new JsonDocument().append("player", player).append("target", target).append("type", message);
        coreAPI.getCloudManager().sendCloudMessage("bukkit", "friendrequest_update", jsonDocument);
    }

    @Getter
    @Setter
    public static class Friend {
        private String value, signature;
        private UUID uuid;
        private boolean isOnline;
        private long lastJoin;
        private String name;
        private PlayerRank playerRank;
        private String currentServer;
    }

    private final HashMap<UUID, FriendEntry> friendEntryMap = new HashMap<>();

    public FriendEntry getFriendEntry(UUID uuid) {
        return friendEntryMap.computeIfAbsent(uuid, ignore -> new FriendEntry(uuid, coreAPI));
    }

    public void removeFriendEntry(UUID uuid) {
        friendEntryMap.remove(uuid);
    }

    public void updateFriendEntry(UUID uuid, String data, String extra) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        friendEntry.updateFriendEntry(uuid, data, extra);
    }

    public void updateFriendRequestEntry(UUID uuid, String data) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        friendEntry.updateFriendRequestEntry(uuid, data);
    }

    public void loadFriendEntry(UUID uuid, boolean isRequest) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        friendEntry.loadFriendEntry(uuid, isRequest);
    }

    public void loadFriendEntryAsync(UUID uuid, boolean isRequest) {
        coreAPI.getExecutor().execute(() -> loadFriendEntry(uuid, isRequest));
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

    public List<Friend> getFriendList(UUID uuid, int page, FriendEntry.SortOption sortOption) {
        FriendEntry friendEntry = getFriendEntry(uuid);
        List<Friend> list = new ArrayList<>(friendEntry.friendCache.values());
        list.sort(sortOption.getComparator());
        return friendEntry.getListForPage(page, list, 10);
    }

    public ConcurrentHashMap<UUID, Friend> getFriendCache(UUID uuid) {
        return getFriendEntry(uuid).getFriendCache();
    }

    public ConcurrentHashMap<UUID, Friend> getFriendRequestsCache(UUID uuid) {
        return getFriendEntry(uuid).getFriendRequestCache();
    }

    @Getter
    @Setter
    public static class FriendEntry {

        private UUID uuid;

        private CoreAPI coreAPI;

        private final ConcurrentHashMap<UUID, Friend> friendCache = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<UUID, Friend> friendRequestCache = new ConcurrentHashMap<>();


        private int page = 1;
        private SortOption sortOption = SortOption.LASTONLINE_RECENTLY;

        public FriendEntry(UUID uuid, CoreAPI coreAPI) {
            this.uuid = uuid;
            this.coreAPI = coreAPI;

            coreAPI.getFriendService().getEntityAsync(uuid, () -> coreAPI.getFriendService().getRepository().findFirstById(uuid), friendProfile -> {
                if (friendProfile == null) return;


                long start = System.currentTimeMillis();

                friendProfile.getFriendList().forEach(friendUUID -> {
                    loadFriendEntry(friendUUID, false);
                });


                coreAPI.getFriendService().saveEntity(friendProfile, true, true);

                friendProfile.getFriendReqeustsList().forEach(requestUUID -> {
                    loadFriendEntry(requestUUID, true);
                });
                long end = System.currentTimeMillis();

                long time = (end / start);
                // System.out.println("[!] loading friends of " + player.getName() + " in " + time + "ms");

            });

        }

        public void loadFriend(UUID uuid, Friend friend) {
            friendCache.put(uuid, friend);
        }

        public void removeFriend(UUID uuid) {
            friendCache.remove(uuid);
        }

        public Friend getFriend(UUID uuid) {
            return friendCache.get(uuid);
        }

        public void loadFriendEntry(UUID uuid, boolean isRequest) {
            PlayerProfile playerProfile = coreAPI.getPlayerService().getEntity(uuid, () -> coreAPI.getPlayerService().getRepository().findFirstById(uuid));
            SkinProfile skinProfile = coreAPI.getSkinService().getEntity(uuid, () -> coreAPI.getSkinService().getRepository().findFirstById(uuid));

            String value;
            String signature;
            if (skinProfile == null) {
                value = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZk" +
                    "NWJkZTk5NGUwYTY0N2FmMTgyMzY4MWE2MTNjMmJmYzNkOTczNmY4ODlkYmY4YzNiYmJhNWExM2Y4ZWQifX19";
                signature = "";
            } else {
                value = skinProfile.getValue();
                signature = skinProfile.getSignature();
            }


            Friend friend = new Friend();

            PlayerRank playerRank = PlayerRank.valueOf(playerProfile.getRank());

            friend.setUuid(uuid);
            friend.setOnline(playerProfile.isOnline());
            friend.setCurrentServer(playerProfile.getServerName());
            friend.setName(playerProfile.getPlayerName());
            friend.setLastJoin(playerProfile.getLastJoin());
            friend.setPlayerRank(playerRank);
            friend.setValue(value);
            friend.setSignature(signature);

            if (isRequest) {
                friendRequestCache.put(uuid, friend);
            } else {
                friendCache.put(uuid, friend);
            }

        }

        public void loadFriendEntryAsync(UUID uuid, boolean isRequest) {
            coreAPI.getExecutor().execute(() -> loadFriendEntry(uuid, isRequest));
        }

        public void updateFriendEntry(UUID uuid, String data, String extra) {

            if (data.equalsIgnoreCase("add")) {

                loadFriendEntryAsync(uuid, false);

            } else if (data.equalsIgnoreCase("remove")) {

                friendCache.remove(uuid);

            } else if (data.equalsIgnoreCase("server_update")) {

                Friend friend = friendCache.get(uuid);
                if (friend == null) return;
                friend.setCurrentServer(extra);
                friendCache.put(uuid, friend);

            } else if (data.equalsIgnoreCase("online")) {

                Friend friend = friendCache.get(uuid);
                if (friend == null) return;
                friend.setOnline(true);
                friendCache.put(uuid, friend);

            } else if (data.equalsIgnoreCase("offline")) {

                Friend friend = friendCache.get(uuid);
                if (friend == null) return;
                friend.setOnline(false);
                friendCache.put(uuid, friend);

            }

        }

        public void updateFriendRequestEntry(UUID uuid, String data) {
            if (data.equalsIgnoreCase("send")) {
                loadFriendEntryAsync(uuid, true);
            } else if (data.equalsIgnoreCase("remove")) {
                friendRequestCache.remove(uuid);
            }
        }


        private List<Friend> getListForPage(int page, List<Friend> list, int slotsPerPage) {
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
    }
}

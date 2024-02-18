package de.teamholy.core.api.entities.friend.entry;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.api.manager.FriendManager;
import de.teamholy.core.api.utility.PlayerRank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
@Getter
@Setter
public class FriendEntry {

    private UUID uuid;

    private CoreAPI coreAPI;

    private final HashMap<UUID, Friend> friendCache = new HashMap<>();
    private final HashMap<UUID, Friend> friendRequestCache = new HashMap<>();

    private int page = 1;
    private FriendManager.SortOption sortOption = FriendManager.SortOption.LASTONLINE_RECENTLY;

    public FriendEntry(UUID uuid, CoreAPI coreAPI) {
        this.uuid = uuid;
        this.coreAPI = coreAPI;

        loadFriends();
    }

    public void loadFriends() {
        coreAPI.getFriendService().getEntityAsync(uuid, () -> coreAPI.getFriendService().getRepository().findFirstById(uuid), friendProfile -> {
            if (friendProfile == null) return;

            friendProfile.getFriendList().forEach(friendUUID -> {
                loadFriendEntry(friendUUID, false);
            });


            coreAPI.getFriendService().saveEntity(friendProfile, true, true);

            friendProfile.getFriendReqeustsList().forEach(requestUUID -> {
                loadFriendEntry(requestUUID, true);
            });
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
            if (friendCache.isEmpty()) return;

            Friend friend = friendCache.get(uuid);
            if (friend == null) return;
            friend.setCurrentServer(extra);
            friendCache.put(uuid, friend);

        } else if (data.equalsIgnoreCase("online")) {
            if (friendCache.isEmpty()) return;

            Friend friend = friendCache.get(uuid);
            if (friend == null) return;
            friend.setOnline(true);
            friendCache.put(uuid, friend);

        } else if (data.equalsIgnoreCase("offline")) {
            if (friendCache.isEmpty()) return;


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


}

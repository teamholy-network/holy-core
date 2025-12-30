package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.api.utility.CustomBanner;
import de.teamholy.core.api.utility.PartyInviteAllowance;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.ProxyManager;
import de.teamholy.core.bungee.util.Pair;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

/**
 * The PostLoginListener class handles various actions and profile management
 * associated with players' post-login events on a proxy server. It implements
 * the Listener interface and integrates with ProxyManager for backend operations.
 *
 * This listener is responsible for:
 * - Sending a welcome message to players.
 * - Performing security checks (e.g., checking proxy connections and blocked ASN).
 * - Managing and loading player profiles, including friend profiles, punish history,
 *   game profiles, perk profiles, staff profiles, and clan-related profiles.
 * - Unlocking and managing special perks, such as the Rainbow Clay Perk.
 * - Handling interaction with staff and notifying them.
 * - Handling friend notifications, such as online friends, friend requests, and login updates.
 * - Updating and saving player-related data to persistent storage.
 * - Managing report statuses for players.
 *
 * Fields:
 * - BLOCKED_ASNS: Contains a collection of blocked Autonomous System Numbers (ASN) for security purposes.
 * - RAINBOW_CLAY_PERK_ID: Identifier for the special Rainbow Clay Perk.
 * - RAINBOW_CLAY_REQUIRED_TIME: Time duration required to unlock the Rainbow Clay Perk.
 * - proxyManager: The manager for proxy-related operations and utilities.
 *
 * Methods:
 * - Constructor to initialize the PostLoginListener with a ProxyManager.
 * - onLogin(PostLoginEvent loginEvent): Event handler method triggered when a player logs in. It performs
 *   security checks, profile handling, and other actions based on the player's connection and status.
 * - Private methods for sending welcome messages, performing security checks, managing profiles, unlocking perks,
 *   and various profile-related tasks such as loading, creating, updating, and saving user data.
 * - Utility methods to notify staff and friends about login events and handle notifications for players.
 *
 * This class ensures that all necessary procedures are executed to provide a secure and engaging
 * environment for players upon logging into the server.
 */
public record PostLoginListener(ProxyManager proxyManager) implements Listener {

    private static final String[] BLOCKED_ASNS = {"31163"};
    private static final long RAINBOW_CLAY_PERK_ID = 30L;
    private static final long RAINBOW_CLAY_REQUIRED_TIME = 288_000_000L;

    public PostLoginListener(ProxyManager proxyManager) {
        this.proxyManager = Objects.requireNonNull(proxyManager, "ProxyManager cannot be null");
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onLogin(PostLoginEvent loginEvent) {
        ProxiedPlayer player = loginEvent.getPlayer();

        sendWelcomeMessage(player);

        String ipAddress = player.getSocketAddress().toString().split(":")[0].replace("/", "");

        if (!player.hasPermission("teamholy.joinfilter.bypass")) {
            performSecurityChecks(player, ipAddress);
        }

        handlePlayerProfile(player, ipAddress);
    }

    private void sendWelcomeMessage(ProxiedPlayer player) {
        player.sendMessage(new TextComponent(""));
        player.sendMessage(new TextComponent("          §f§l" + BungeeTranslateAPI.translatePlaceholder(
            player, "WELCOME ON {}", "§6§lTEAMHOLY") + "          "));
        player.sendMessage(new TextComponent(" "));
        player.sendMessage(new TextComponent("§3Discord §8» §7https://discord.gg/teamholy"));
        player.sendMessage(new TextComponent("§cStore §8» §7https://shop.teamholy.de/"));
        player.sendMessage(new TextComponent("§6Vote §8» §7https://teamholy.de/vote"));
        player.sendMessage(new TextComponent("§5" + BungeeTranslateAPI.translate(player, "Website") + " §8» §7https://teamholy.de"));
        player.sendMessage(new TextComponent(""));
    }

    private void performSecurityChecks(ProxiedPlayer player, String ipAddress) {
        BungeeCore.getAPI().getExecutor().execute(() -> {
            checkProxyConnection(player, ipAddress);
            checkBlockedASN(player, ipAddress);
        });
    }

    private void checkProxyConnection(ProxiedPlayer player, String ipAddress) {
        proxyManager.containsProxy(ipAddress).thenAccept(isKnownProxy -> {
            if (isKnownProxy) {
                disconnectForProxy(player, ipAddress);
            } else {
                proxyManager.checkProxy(ipAddress, (isProxy, countryName, org) -> {
                    if (isProxy) {
                        disconnectForProxy(player, ipAddress);
                        proxyManager.addProxy(ipAddress);
                    } else {
                        proxyManager.removeProxy(ipAddress);
                    }
                });
            }
        });
    }

    private void disconnectForProxy(ProxiedPlayer player, String ipAddress) {
        player.disconnect(new TextComponent(proxyManager.kickMessage));
        proxyManager.sendProxyWarning(player, ipAddress);
    }

    private void checkBlockedASN(ProxiedPlayer player, String ipAddress) {
        proxyManager.checkASN(ipAddress, asn -> {
            if (asn != null && isAsnBlocked(asn)) {
                player.disconnect(new TextComponent(proxyManager.kickMessage));
                proxyManager.sendAsnWarning(player, ipAddress, asn);
            }
        });
    }

    private boolean isAsnBlocked(String asn) {
        return Arrays.stream(BLOCKED_ASNS)
            .anyMatch(asn::contains);
    }

    private void handlePlayerProfile(ProxiedPlayer player, String ipAddress) {
        Pair<PlayerProfile, Boolean> profileLoadResult = loadOrCreatePlayerProfile(player, ipAddress);
        PlayerProfile playerProfile = profileLoadResult.getLeft();
        boolean isNewPlayer = profileLoadResult.getRight();

        if (!isNewPlayer) {
            if (playerProfile.isOnline()) {
                player.disconnect(new TextComponent("§cYou are already connected to this proxy!\n§7Please wait a moment and try again."));
                return;
            }
            playerProfile.setOnline(true);
            playerProfile.setLastJoin(System.currentTimeMillis());
        }

        FriendProfile friendProfile = loadOrCreateFriendProfile(player.getUniqueId(), isNewPlayer);
        PunishHistoryProfile punishHistoryProfile = loadOrCreatePunishHistoryProfile(player.getUniqueId(), isNewPlayer);
        GameProfile gameProfile = loadOrCreateGameProfile(player.getUniqueId(), isNewPlayer);
        PerkPlayerProfile perkPlayerProfile = loadOrCreatePerkPlayerProfile(player.getUniqueId(), isNewPlayer);

        if (!isNewPlayer) {
            updateExistingPlayerProfile(player, playerProfile, ipAddress);
        }

        handleSpecialPerks(player, perkPlayerProfile, playerProfile);
        handleStaffProfile(player);
        handleFriendNotifications(player, friendProfile);
        handleClanProfile(player);

        saveAllProfiles(playerProfile, friendProfile, punishHistoryProfile, gameProfile, perkPlayerProfile, isNewPlayer);
        updateReportStatus(player);
    }

    private Pair<PlayerProfile, Boolean> loadOrCreatePlayerProfile(ProxiedPlayer player, String ipAddress) {
        PlayerProfile profile = BungeeCore.getAPI().getPlayerService().getEntity(
            player.getUniqueId(),
            () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId())
        );

        boolean isNewPlayer = profile == null;
        if (isNewPlayer) {
            profile = createNewPlayerProfile(player, ipAddress);
        }

        return new Pair<PlayerProfile, Boolean>(profile, isNewPlayer);
    }

    private PlayerProfile createNewPlayerProfile(ProxiedPlayer player, String ipAddress) {
        PlayerProfile profile = new PlayerProfile();
        profile.setPlayerId(player.getUniqueId());
        profile.setPlayerName(player.getName());
        profile.setIp(ipAddress);
        profile.setCoins(0L);
        profile.setOnlineTime(0L);
        profile.setRank(PlayerRank.PLAYER.toString());
        profile.setStatsResetTokens(0L);
        profile.setJoinMeTokens(0L);
        profile.setAutoNick(false);
        profile.setCollectables(new HashMap<>());

        long now = System.currentTimeMillis();
        profile.setFirstJoin(now);
        profile.setLastJoin(now);

        return profile;
    }

    private void updateExistingPlayerProfile(ProxiedPlayer player, PlayerProfile profile, String ipAddress) {
        BungeeCore.getAPI().getExecutor().execute(() -> {

            if (!profile.getPlayerName().equalsIgnoreCase(player.getName())) {
                profile.setPlayerName(player.getName());
            }

            if (!profile.getIp().equalsIgnoreCase(ipAddress)) {
                profile.setIp(ipAddress);
            }

            if (!player.hasPermission("markupapi.nick") && profile.isAutoNick()) {
                profile.setAutoNick(false);
            }

            updateRankFromPermissions(player, profile);

            BungeeCore.getAPI().getPlayerService().saveEntity(profile, true, true);
        });
    }

    private void updateRankFromPermissions(ProxiedPlayer player, PlayerProfile profile) {
        IPermissionUser permissionUser = CloudNetDriver.getInstance()
            .getPermissionManagement()
            .getUser(player.getUniqueId());

        if (permissionUser != null) {
            String group = CloudNetDriver.getInstance()
                .getPermissionManagement()
                .getHighestPermissionGroup(permissionUser)
                .getName()
                .toUpperCase(Locale.ROOT);

            PlayerRank playerRank = PlayerRank.fromString(group);
            String normalizedRank = playerRank.name();

            if (!normalizedRank.equalsIgnoreCase(profile.getRank())) {
                profile.setRank(normalizedRank);
            }
        }
    }

    private FriendProfile loadOrCreateFriendProfile(UUID playerId, boolean isNewPlayer) {
        if (isNewPlayer) {
            return createNewFriendProfile(playerId);
        }

        return BungeeCore.getAPI().getFriendService().getEntity(
            playerId,
            () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(playerId)
        );
    }

    private FriendProfile createNewFriendProfile(UUID playerId) {
        FriendProfile profile = new FriendProfile();
        profile.setPlayerId(playerId);
        profile.setAllowFriendRequests(true);
        profile.setPartyInviteAllowance(PartyInviteAllowance.EVERYONE);
        profile.setAllowFriendJump(true);
        profile.setFriendList(new ArrayList<>());
        profile.setFriendReqeustsList(new ArrayList<>());
        return profile;
    }

    private PunishHistoryProfile loadOrCreatePunishHistoryProfile(UUID playerId, boolean isNewPlayer) {
        if (isNewPlayer) {
            return createNewPunishHistoryProfile(playerId);
        }

        return BungeeCore.getAPI().getPunishHistoryService().getEntity(
            playerId,
            () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(playerId)
        );
    }

    private PunishHistoryProfile createNewPunishHistoryProfile(UUID playerId) {
        PunishHistoryProfile profile = new PunishHistoryProfile();
        profile.setPlayerId(playerId);
        profile.setBanProfileMap(new HashMap<>());
        profile.setMuteProfileMap(new HashMap<>());
        return profile;
    }

    private GameProfile loadOrCreateGameProfile(UUID playerId, boolean isNewPlayer) {
        if (isNewPlayer) {
            return createNewGameProfile(playerId);
        }

        return BungeeCore.getAPI().getGameService().getEntity(
            playerId,
            () -> BungeeCore.getAPI().getGameService().getRepository().findFirstById(playerId)
        );
    }

    private GameProfile createNewGameProfile(UUID playerId) {
        GameProfile profile = new GameProfile();
        profile.setPlayerId(playerId);
        profile.setSettingsMap(new HashMap<>());
        profile.setStatsMap(new HashMap<>());
        return profile;
    }

    private PerkPlayerProfile loadOrCreatePerkPlayerProfile(UUID playerId, boolean isNewPlayer) {
        if (isNewPlayer) {
            return createNewPerkPlayerProfile(playerId);
        }

        return BungeeCore.getAPI().getPerkPlayerService().getEntity(
            playerId,
            () -> BungeeCore.getAPI().getPerkPlayerService().getRepository().findFirstById(playerId)
        );
    }

    private PerkPlayerProfile createNewPerkPlayerProfile(UUID playerId) {
        PerkPlayerProfile profile = new PerkPlayerProfile();
        profile.setPlayerId(playerId);
        profile.setBlockPerk(0);
        profile.setStickPerk(100);
        profile.setChatPerk(200);
        profile.setOwnedPerks(new ArrayList<>());
        profile.setCustomBanner(new CustomBanner());
        return profile;
    }

    private void handleSpecialPerks(ProxiedPlayer player, PerkPlayerProfile perkProfile, PlayerProfile playerProfile) {
        if (shouldUnlockRainbowClayPerk(perkProfile, playerProfile)) {
            unlockRainbowClayPerk(player, perkProfile);
        }
    }

    private boolean shouldUnlockRainbowClayPerk(PerkPlayerProfile perkProfile, PlayerProfile playerProfile) {
        return !perkProfile.getOwnedPerks().contains((int) RAINBOW_CLAY_PERK_ID)
            && playerProfile.getOnlineTime() >= RAINBOW_CLAY_REQUIRED_TIME;
    }

    private void unlockRainbowClayPerk(ProxiedPlayer player, PerkPlayerProfile perkProfile) {
        perkProfile.getOwnedPerks().add((int) RAINBOW_CLAY_PERK_ID);
        player.sendMessage(new TextComponent("§6Perk §8× §7" + BungeeTranslateAPI.translatePlaceholder(
            player,
            "You have unlocked the {} Clay Perk §7for playing §b80 hours",
            "§4R§ca§6i§en§ab§2o§bw"
        )));
    }

    private void handleStaffProfile(ProxiedPlayer player) {
        if (!player.hasPermission("teamholy.team")) {
            return;
        }

        StaffProfile staffProfile = loadOrCreateStaffProfile(player.getUniqueId());
        boolean isNewStaff = staffProfile.getBanProfileList() == null || staffProfile.getBanProfileList().isEmpty();

        BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, isNewStaff);
        notifyStaffMembers(player);
    }

    private StaffProfile loadOrCreateStaffProfile(UUID playerId) {
        StaffProfile profile = BungeeCore.getAPI().getStaffService().getEntity(
            playerId,
            () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(playerId)
        );

        if (profile == null) {
            profile = createNewStaffProfile(playerId);
        }

        return profile;
    }

    private StaffProfile createNewStaffProfile(UUID playerId) {
        StaffProfile profile = new StaffProfile();
        profile.setPlayerId(playerId);
        profile.setNotify(true);
        profile.setBanProfileList(new ArrayList<>());
        profile.setMuteProfileList(new ArrayList<>());
        profile.setReportList(new ArrayList<>());
        return profile;
    }

    private void notifyStaffMembers(ProxiedPlayer player) {
        String playerName = BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName();

        BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffMember -> {
            staffMember.sendMessage(new TextComponent("§cTeam §8× " + BungeeTranslateAPI.translatePlaceholder(
                staffMember,
                "{} §7is now §aonline",
                playerName
            )));
        });
    }

    private void handleFriendNotifications(ProxiedPlayer player, FriendProfile friendProfile) {
        if (friendProfile == null) {
            return;
        }

        String playerName = BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName();
        BungeeCore.getAPI().getFriendManager().sendFriendUpdateData(player.getUniqueId(), null, "online", null);

        notifyFriendsAboutLogin(player, friendProfile, playerName);
        displayOnlineFriends(player, friendProfile);
        displayFriendRequests(player, friendProfile);
    }

    private void notifyFriendsAboutLogin(ProxiedPlayer player, FriendProfile friendProfile, String playerName) {
        friendProfile.getFriendList().forEach(friendId -> {
            ProxiedPlayer friend = ProxyServer.getInstance().getPlayer(friendId);
            if (friend != null) {
                friend.sendMessage(new TextComponent("§6Friend §8× §7" + BungeeTranslateAPI.translatePlaceholder(
                    friend,
                    "Your friend {} is now §aonline",
                    playerName + "§7"
                )));
            }
        });
    }

    private void displayOnlineFriends(ProxiedPlayer player, FriendProfile friendProfile) {
        List<String> onlineFriends = new ArrayList<>();

        friendProfile.getFriendList().forEach(friendId -> {
            ProxiedPlayer friend = ProxyServer.getInstance().getPlayer(friendId);
            if (friend != null) {
                String friendName = BungeeCore.getInstance().getPlayerColor(friend.getUniqueId()) + friend.getName();
                onlineFriends.add(friendName);
            }
        });

        int count = onlineFriends.size();
        String pluralForm = (count >= 2 ? "s" : "");
        String countText = count == 0
            ? "§c" + BungeeTranslateAPI.translate(player, "no §7friend")
            : "§a" + count + " §7" + BungeeTranslateAPI.translate(player, "friend" + pluralForm);

        player.sendMessage(new TextComponent("§6Friend §8× §7" + BungeeTranslateAPI.translatePlaceholder(
            player,
            "There " + (count <= 1 ? "is" : "are") + " currently " + (count == 0 ? "{}" : "{}") + " online",
            countText
        )));

        if (!onlineFriends.isEmpty()) {
            player.sendMessage(new TextComponent("§6Friend §8× " + String.join("§7, ", onlineFriends)));
        }
    }

    private void displayFriendRequests(ProxiedPlayer player, FriendProfile friendProfile) {
        int requestCount = friendProfile.getFriendReqeustsList().size();

        if (requestCount == 0) {
            return;
        }

        String requestWord = requestCount == 1 ? "request" : "requests";
        String message = "§6Friend §8× §7" + BungeeTranslateAPI.translatePlaceholder(
            player,
            "You currently have {} open friend " + requestWord,
            "§a" + requestCount + "§7"
        );

        player.sendMessage(
            new ComponentBuilder(message)
                .append(" §8(§a§l" + BungeeTranslateAPI.translate(player, "CLICK") + "§8)")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend requests"))
                .create()
        );
    }

    private void handleClanProfile(ProxiedPlayer player) {
        ClanPlayerProfile clanProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(
            player.getUniqueId(),
            () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId())
        );

        if (clanProfile != null) {
            BungeeCore.getAPI().getClanPlayerService().saveEntity(clanProfile, true, false);
        }
    }

    private void saveAllProfiles(PlayerProfile playerProfile, FriendProfile friendProfile,
                                 PunishHistoryProfile punishHistoryProfile, GameProfile gameProfile,
                                 PerkPlayerProfile perkPlayerProfile, boolean saveToDatabase) {

        BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile, true, saveToDatabase);

        if (friendProfile != null) {
            BungeeCore.getAPI().getFriendService().saveEntity(friendProfile, true, saveToDatabase);
        }

        if (punishHistoryProfile != null) {
            BungeeCore.getAPI().getPunishHistoryService().saveEntity(punishHistoryProfile, true, saveToDatabase);
        }

        if (gameProfile != null) {
            BungeeCore.getAPI().getGameService().saveEntity(gameProfile, true, saveToDatabase);
        }

        if (perkPlayerProfile != null) {
            BungeeCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, saveToDatabase);
        }
    }

    private void updateReportStatus(ProxiedPlayer player) {
        BungeeCore.getAPI().getReportManager().getAllReports().values().forEach(report -> {
            if (report.getTarget().equals(player.getUniqueId())) {
                report.setTargetOnline(true);
                BungeeCore.getAPI().getReportManager().addReport(report);
            }
        });
    }
}
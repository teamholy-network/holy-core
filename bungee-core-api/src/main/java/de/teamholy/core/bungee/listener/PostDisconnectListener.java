package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.utility.Report;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.friend.FriendCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * The PostDisconnectListener class listens for PlayerDisconnectEvent and
 * performs various actions when a player disconnects from the proxy server.
 *
 * This listener is responsible for managing player-related data upon disconnection,
 * including updating player profiles, handling clan and friend notifications,
 * processing outstanding reports, notifying staff members, and performing cache cleanup.
 *
 * Responsibilities include:
 * - Updating the player's online status in their profile.
 * - Managing clan profiles to determine if all members of a clan are offline and
 *   taking appropriate action.
 * - Sending friend offline notifications and updates when a player disconnects.
 * - Handling report updates related to the disconnected player as a target or viewer.
 * - Notifying staff members when a staff player disconnects.
 * - Cleaning up and updating expiration times for player-related caches.
 */
public class PostDisconnectListener implements Listener {

    public PostDisconnectListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        updatePlayerProfileImmediate(player);

        handleClanProfile(player);
        handleFriendNotifications(player);
        handleReportUpdates(player);
        notifyStaffMembers(player);
        cleanupCaches(player);

        BungeeCore.getAPI().getNickManager().removeNick(player.getUniqueId());
    }

    private void updatePlayerProfileImmediate(ProxiedPlayer player) {
        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(
            player.getUniqueId(),
            () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId())
        );

        if (playerProfile != null) {
            playerProfile.setOnline(false);
            BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile, true, false);

            BungeeCore.getAPI().getExecutor().execute(() -> {
                BungeeCore.getAPI().getPlayerService().getRepository().save(playerProfile);
            });
        }
    }

    private void handleClanProfile(ProxiedPlayer player) {
        ClanPlayerProfile clanPlayerProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(
            player.getUniqueId(),
            () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId())
        );

        if (clanPlayerProfile == null) {
            return;
        }

        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId());

        if (clan == null) {
            return;
        }

        boolean allOffline = clan.getMembers().stream()
            .noneMatch(member -> BungeeCore.getInstance().getBungeePlayerManager().isOnline(member));

        if (allOffline) {
            BungeeCore.getAPI().getClanManager().unforce(clanPlayerProfile.getClanId());
        }
    }

    private void handleFriendNotifications(ProxiedPlayer player) {
        FriendProfile friendProfile = BungeeCore.getAPI().getFriendService().getEntity(
            player.getUniqueId(),
            () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(player.getUniqueId())
        );

        if (friendProfile == null) {
            return;
        }

        String playerName = BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName();

        BungeeCore.getAPI().getFriendManager().sendFriendUpdateData(player.getUniqueId(), null, "offline", null);
        FriendCommand.LASTREPLYS.remove(player.getUniqueId());

        friendProfile.getFriendList().forEach(friendId -> {
            ProxiedPlayer friend = ProxyServer.getInstance().getPlayer(friendId);
            if (friend != null) {
                friend.sendMessage(new TextComponent("§6Friend §8× §7" + ("Your friend " + (playerName + "§7") + " is now §coffline")));
            }
        });
    }

    private void handleReportUpdates(ProxiedPlayer player) {
        BungeeCore.getAPI().getReportManager().getAllReports().values().forEach(report -> {
            if (report.getTarget().equals(player.getUniqueId())) {
                updateReportForTarget(report);
            } else if (report.getViewer() != null && report.getViewer().equals(player.getUniqueId())) {
                report.setViewer(null);
                report.setTargetOnline(false);
                BungeeCore.getAPI().getReportManager().addReport(report);
            }
        });
    }

    private void updateReportForTarget(Report report) {
        if (report.getViewer() != null) {
            ProxiedPlayer viewer = ProxyServer.getInstance().getPlayer(report.getViewer());

            if (viewer != null) {
                String targetName = BungeeCore.getInstance().getPlayerColor(report.getTarget())
                    + BungeeCore.getAPI().getUuidManager().getName(report.getTarget());

                viewer.sendMessage(new TextComponent("§c" + ("The player " + (targetName + "§c") + " is now offline")));
            }
        }

        report.setViewer(null);
        report.setTargetOnline(false);
        BungeeCore.getAPI().getReportManager().addReport(report);
    }

    private void notifyStaffMembers(ProxiedPlayer player) {
        if (!player.hasPermission("teamholy.team")) {
            return;
        }

        String playerName = BungeeCore.getInstance().getPlayerColor(player.getUniqueId()) + player.getName();

        BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffMember -> {
            staffMember.sendMessage(new TextComponent("§cTeam §8× " + ((playerName + "§7") + " is now §coffline")));
        });
    }

    private void cleanupCaches(ProxiedPlayer player) {
        UUID playerId = player.getUniqueId();

        BungeeCore.getAPI().getStaffService().getRedisCache()
            .updateEntryExpiration(playerId, 15, TimeUnit.MINUTES, 0, TimeUnit.MINUTES);

        BungeeCore.getAPI().getClanPlayerService().getRedisCache()
            .updateEntryExpiration(playerId, 15, TimeUnit.MINUTES, 0, TimeUnit.MINUTES);

        BungeeCore.getAPI().getFriendService().getRedisCache()
            .updateEntryExpiration(playerId, 15, TimeUnit.MINUTES, 0, TimeUnit.MINUTES);

        BungeeCore.getAPI().getSkinService().getRedisCache()
            .updateEntryExpiration(playerId, 15, TimeUnit.MINUTES, 0, TimeUnit.MINUTES);

        BungeeCore.getAPI().getMuteService().getRedisCache()
            .updateEntryExpiration(playerId, 15, TimeUnit.MINUTES, 0, TimeUnit.MINUTES);

        BungeeCore.getAPI().getGameService().getRedisCache()
            .updateEntryExpiration(playerId, 15, TimeUnit.MINUTES, 0, TimeUnit.MINUTES);

        BungeeCore.getAPI().getPunishHistoryService().getRedisCache()
            .updateEntryExpiration(playerId, 15, TimeUnit.MINUTES, 0, TimeUnit.MINUTES);

        BungeeCore.getAPI().getPerkPlayerService().getRedisCache()
            .updateEntryExpiration(playerId, 15, TimeUnit.MINUTES, 0, TimeUnit.MINUTES);
    }
}
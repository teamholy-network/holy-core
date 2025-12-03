package de.teamholy.core.bungee.manager;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.commands.team.AdminChatCommand;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The BungeePlayerManager class provides utility methods for managing and interacting with
 * BungeeCord players. It allows checking player statuses, sending messages to specific
 * groups of players, and retrieving player-related information.
 *
 * This class leverages CoreAPI as a dependency for UUID management and various services
 * related to player and staff data.
 *
 * Features include:
 * - Checking if a player is online by name or UUID.
 * - Parsing online/offline status into respective color codes.
 * - Sending messages to clan members or staff/admin groups.
 * - Retrieving collections of players based on specific criteria.
 * - Handling player-specific configurations such as notification settings or rank checks.
 */
@Slf4j
public record BungeePlayerManager(CoreAPI coreAPI) {

    private static final String PERMISSION_TEAM = "teamholy.team";
    private static final String COLOR_ONLINE = "§a";
    private static final String COLOR_OFFLINE = "§c";

    public boolean isOnline(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        UUID uuid = coreAPI.getUuidManager().getUUID(name);
        return isOnline(uuid);
    }

    public boolean isOnline(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        return getPlayer(uuid)
            .map(ProxiedPlayer::isConnected)
            .orElse(false);
    }

    public String parseOnlinePrefix(String name) {
        if (name == null || name.isEmpty()) {
            return COLOR_OFFLINE;
        }

        UUID uuid = coreAPI.getUuidManager().getUUID(name);
        return parseOnlinePrefix(uuid);
    }

    public String parseOnlinePrefix(UUID uuid) {
        return isOnline(uuid) ? COLOR_ONLINE : COLOR_OFFLINE;
    }

    public void sendClanMessage(Clan clan, String message) {
        if (clan == null || message == null) {
            log.warn("Attempted to send clan message with null clan or message");
            return;
        }

        getClanMessagePlayers(clan).forEach(player -> player.sendMessage(message));
    }

    public Set<ProxiedPlayer> getClanMessagePlayers(Clan clan) {
        if (clan == null || clan.getMembers() == null) {
            return ConcurrentHashMap.newKeySet();
        }

        return clan.getMembers().stream()
            .map(this::getPlayer)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(ProxiedPlayer::isConnected)
            .collect(Collectors.toCollection(ConcurrentHashMap::newKeySet));
    }

    public Set<ProxiedPlayer> getStaffNotifyPlayers() {
        return getAllPlayers().stream()
            .filter(player -> player.hasPermission(PERMISSION_TEAM))
            .filter(this::hasNotificationsEnabled)
            .collect(Collectors.toCollection(ConcurrentHashMap::newKeySet));
    }

    public void notifyStaff(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("Attempted to notify staff with null or empty message");
            return;
        }

        ProxyServer.getInstance().getConsole().sendMessage(message);

        getStaffNotifyPlayers().forEach(player -> {
            try {
                player.sendMessage(message);
            } catch (Exception e) {
                log.error("Failed to send notification to player: {}", player.getName(), e);
            }
        });
    }

    public void notifyAdmin(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("Attempted to notify admins with null or empty message");
            return;
        }

        ProxyServer.getInstance().getConsole().sendMessage(message);

        getAllPlayers().stream()
            .filter(this::isAdminRank)
            .filter(this::hasNotificationsEnabled)
            .forEach(player -> {
                try {
                    player.sendMessage(message);
                } catch (Exception e) {
                    log.error("Failed to send admin notification to player: {}", player.getName(), e);
                }
            });
    }

    private Optional<ProxiedPlayer> getPlayer(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(ProxyServer.getInstance().getPlayer(uuid));
    }

    private Collection<ProxiedPlayer> getAllPlayers() {
        return ProxyServer.getInstance().getPlayers();
    }

    private boolean hasNotificationsEnabled(ProxiedPlayer player) {
        try {
            StaffProfile staffProfile = getStaffProfile(player.getUniqueId());
            return staffProfile != null && staffProfile.isNotify();
        } catch (Exception e) {
            log.error("Failed to check notification status for player: {}", player.getName(), e);
            return false;
        }
    }

    private boolean isAdminRank(ProxiedPlayer player) {
        try {
            PlayerProfile playerProfile = getPlayerProfile(player.getUniqueId());
            return playerProfile != null
                && AdminChatCommand.adminRanks.contains(playerProfile.getRank());
        } catch (Exception e) {
            log.error("Failed to check admin rank for player: {}", player.getName(), e);
            return false;
        }
    }

    private StaffProfile getStaffProfile(UUID uuid) {
        return BungeeCore.getAPI().getStaffService().getEntity(
            uuid,
            () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(uuid)
        );
    }

    private PlayerProfile getPlayerProfile(UUID uuid) {
        return BungeeCore.getAPI().getPlayerService().getEntity(
            uuid,
            () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(uuid)
        );
    }
}
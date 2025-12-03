package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.ban.BanService;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryProfile;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.util.BanUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Handles player login events to enforce bans and check for ban bypass attempts.
 * This listener prevents banned users from logging in and takes appropriate actions
 * based on the ban status and associated conditions.
 *
 * Features include:
 * - Blocking login attempts for players with active bans.
 * - Archiving expired bans into the punish history system.
 * - Checking for and addressing ban bypass attempts from players using the same IP address as banned accounts.
 * - Informing staff members about relevant ban and unban activities.
 *
 * Dependencies:
 * - Requires a {@link BanService} to manage bans and interact with the ban repository.
 * - Uses {@link BungeeCore} for accessing core functionalities such as ban services,
 *   player services, and staff notification mechanisms.
 *
 * The class automatically registers itself as a login event listener upon instantiation.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BanLoginListener implements Listener {

    private static final long BAN_BYPASS_DURATION = TimeUnit.HOURS.toMillis(5);
    private static final int DELAYED_KICK_SECONDS = 2;
    private static final String PREMIUM_MESSAGE = "§cPlease join with §6premium.teamholy.net §cif you are a premium account.";

    BanService banService;
    BungeeCore bungeeCore;

    public BanLoginListener(BungeeCore bungeeCore) {
        this.bungeeCore = bungeeCore;
        this.banService = bungeeCore.getCoreAPI().getBanService();
        ProxyServer.getInstance().getPluginManager().registerListener(bungeeCore, this);
    }

    @EventHandler(priority = 2)
    public void onLogin(LoginEvent loginEvent) {
        UUID playerId = loginEvent.getConnection().getUniqueId();
        String ipAddress = extractIpAddress(loginEvent);

        BanProfile banProfile = loadBanProfile(playerId);

        if (banProfile != null) {
            handleExistingBan(loginEvent, playerId, banProfile);
        } else {
            checkForBanBypass(loginEvent, ipAddress);
        }
    }

    private String extractIpAddress(LoginEvent loginEvent) {
        return loginEvent.getConnection().getSocketAddress().toString()
            .split(":")[0]
            .replace("/", "");
    }

    private BanProfile loadBanProfile(UUID playerId) {
        return banService.getEntity(
            playerId,
            () -> banService.getRepository().findFirstById(playerId)
        );
    }

    private void handleExistingBan(LoginEvent loginEvent, UUID playerId, BanProfile banProfile) {
        if (banProfile.active()) {
            enforceActiveBan(loginEvent, banProfile);
        } else {
            archiveExpiredBan(playerId, banProfile);
        }
    }

    private void enforceActiveBan(LoginEvent loginEvent, BanProfile banProfile) {
        if (banProfile.getWebLinkId() == null) {
            BanUtil.addBanWebLinkIdToProfile(banProfile);
        }

        String banMessage = BanUtil.generateBanScreen(banProfile);
        loginEvent.setCancelled(true);
        loginEvent.setCancelReason(new TextComponent(banMessage));
    }

    private void archiveExpiredBan(UUID playerId, BanProfile banProfile) {
        PunishHistoryProfile historyProfile = loadOrCreatePunishHistory(playerId);

        historyProfile.getBanProfileMap().put(UUID.randomUUID().toString(), banProfile);

        bungeeCore.getCoreAPI().getPunishHistoryService().saveEntity(historyProfile, false, true);
        notifyStaffOfUnban(banProfile);
        banService.deleteEntity(banProfile);
    }

    private PunishHistoryProfile loadOrCreatePunishHistory(UUID playerId) {
        PunishHistoryProfile profile = bungeeCore.getCoreAPI().getPunishHistoryService().getEntity(
            playerId,
            () -> bungeeCore.getCoreAPI().getPunishHistoryService().getRepository().findFirstById(playerId)
        );

        if (profile == null) {
            profile = new PunishHistoryProfile();
            profile.setPlayerId(playerId);
        }

        return profile;
    }

    private void notifyStaffOfUnban(BanProfile banProfile) {
        bungeeCore.getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffMember -> {
            staffMember.sendMessage(BanUtil.generateUnbanMessage(staffMember, "console", banProfile));
        });
    }

    private void checkForBanBypass(LoginEvent loginEvent, String ipAddress) {
        List<PlayerProfile> profilesWithSameIp = findProfilesByIp(ipAddress);

        if (!processBanBypassCheck(profilesWithSameIp, loginEvent) && !loginEvent.isCancelled()) {
            profilesWithSameIp = bungeeCore.getCoreAPI().getPlayerService()
                .getRepository()
                .findManyByIp(ipAddress);
            processBanBypassCheck(profilesWithSameIp, loginEvent);
        }
    }

    private List<PlayerProfile> findProfilesByIp(String ipAddress) {
        return bungeeCore.getCoreAPI().getPlayerService()
            .getRedisCache()
            .values()
            .stream()
            .filter(profile -> profile.getIp().equals(ipAddress))
            .collect(Collectors.toList());
    }

    private boolean processBanBypassCheck(List<PlayerProfile> profiles, LoginEvent loginEvent) {
        if (profiles == null || profiles.isEmpty()) {
            return false;
        }

        for (PlayerProfile profile : profiles) {
            BanProfile banProfile = loadBanProfile(profile.getPlayerId());

            if (isBanBypassAttempt(banProfile, profile, loginEvent)) {
                return handleBanBypassAttempt(profile, loginEvent);
            }
        }

        return false;
    }

    private boolean isBanBypassAttempt(BanProfile banProfile, PlayerProfile bannedProfile, LoginEvent loginEvent) {
        if (banProfile == null || !banProfile.active()) {
            return false;
        }

        if (banProfile.getReason().equalsIgnoreCase(Punish.BanReason.BAN_BYPASS.getEnglishText())) {
            return false;
        }

        if (bannedProfile.getPlayerName().equalsIgnoreCase(loginEvent.getConnection().getName())) {
            loginEvent.setCancelled(true);
            loginEvent.setCancelReason(new TextComponent(PREMIUM_MESSAGE));
            return false;
        }

        return true;
    }

    private boolean handleBanBypassAttempt(PlayerProfile bannedProfile, LoginEvent loginEvent) {
        BanProfile bypassBan = createBanBypassProfile(loginEvent.getConnection().getUniqueId(), bannedProfile);

        bungeeCore.getCoreAPI().getBanService().saveEntity(bypassBan, true, true);
        scheduleDelayedKick(loginEvent.getConnection().getName(), bypassBan);
        notifyStaffOfBanBypass(bypassBan);

        return true;
    }

    private BanProfile createBanBypassProfile(UUID bypasserId, PlayerProfile originalBannedProfile) {
        BanProfile banProfile = new BanProfile();
        banProfile.setPlayerId(bypasserId);
        banProfile.setDuration(BAN_BYPASS_DURATION);
        banProfile.setReason(Punish.BanReason.BAN_BYPASS.getEnglishText());
        banProfile.setEvidence("Tried bypassing the ban of " + originalBannedProfile.getPlayerName());
        banProfile.setAuthorId(Punish.getConsoleUuid());
        banProfile.setCreateDate(System.currentTimeMillis());

        return banProfile;
    }

    private void scheduleDelayedKick(String playerName, BanProfile banProfile) {
        BungeeCore.getInstance().getProxy().getScheduler().schedule(
            BungeeCore.getInstance(),
            () -> {
                ProxiedPlayer player = BungeeCore.getInstance().getProxy().getPlayer(playerName);
                if (player != null) {
                    player.disconnect(new TextComponent(BanUtil.generateBanScreen(banProfile)));
                }
            },
            DELAYED_KICK_SECONDS,
            TimeUnit.SECONDS
        );
    }

    private void notifyStaffOfBanBypass(BanProfile banProfile) {
        BungeeCore.getInstance().getBungeePlayerManager().getStaffNotifyPlayers().forEach(staffMember -> {
            staffMember.sendMessage(BanUtil.generateBanMessage(staffMember, banProfile));
        });
    }
}
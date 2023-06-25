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
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/* copyright by Yassino */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BanLoginListener implements Listener {

    BanService punishService;
    BungeeCore bungeeCore;

    public BanLoginListener(BungeeCore bungeeCore) {
        this.bungeeCore = bungeeCore;
        this.punishService = bungeeCore.getCoreAPI().getBanService();
        ProxyServer.getInstance().getPluginManager().registerListener(bungeeCore, this);
    }

    @EventHandler(priority = 1)
    public void onLogin(LoginEvent loginEvent) {


            UUID uuid = loginEvent.getConnection().getUniqueId();

            BanProfile punishProfile = punishService.getEntity(uuid, () -> punishService.getRepository().findFirstById(uuid));

            String ipAddress = loginEvent.getConnection().getAddress().getAddress().getHostAddress();

            if (punishProfile != null) {
                if (punishProfile.active()) {
                    loginEvent.setCancelled(true);
                    loginEvent.setCancelReason(BanUtil.generateBanScreen(punishProfile));
                } else {
                    PunishHistoryProfile punishHistoryProfile = bungeeCore.getCoreAPI().getPunishHistoryService().getEntity(uuid, () -> bungeeCore.getCoreAPI().getPunishHistoryService().getRepository().findFirstById(uuid));
                    if (punishHistoryProfile == null) punishHistoryProfile = new PunishHistoryProfile();

                    punishHistoryProfile.getBanProfileMap().put(UUID.randomUUID().toString(), punishProfile);

                    bungeeCore.getCoreAPI().getPunishHistoryService().saveEntity(punishHistoryProfile, false, true);
                    bungeeCore.getBungeePlayerManager().notifyStaff(BanUtil.generateUnbanMessage("Console", punishProfile));
                    punishService.deleteEntity(punishProfile);
                }
            } else {
                List<PlayerProfile> profileList = bungeeCore.getCoreAPI().getPlayerService().getRedisCache().values().stream()
                        .filter(playerProfile -> playerProfile.getIp().equals(ipAddress)).collect(Collectors.toList());

                if (!filterBanBypass(profileList, loginEvent) && !loginEvent.isCancelled()) {
                    profileList = bungeeCore.getCoreAPI().getPlayerService().getRepository().findManyByIp(ipAddress);
                    filterBanBypass(profileList, loginEvent);
                }
            }
    }

    private boolean filterBanBypass(List<PlayerProfile> profileList, LoginEvent loginEvent) {
        if (profileList == null || profileList.isEmpty()) {
            return false;
        }
        for (PlayerProfile playerAcc : profileList) {


            BanProfile punishProfile = punishService.getEntity(playerAcc.getPlayerId(), () -> punishService.getRepository().findFirstById(playerAcc.getPlayerId()));
            if (punishProfile != null && punishProfile.active() && !punishProfile.getReason().equalsIgnoreCase(Punish.BanReason.BAN_BYPASS.getEnglishText())) {
                Punish.BanReason banReason = Punish.BanReason.BAN_BYPASS;
                BanProfile banProfile = new BanProfile();
                banProfile.setPlayerId(loginEvent.getConnection().getUniqueId());
                banProfile.setDuration(TimeUnit.HOURS.toMillis(5));
                banProfile.setReason(banReason.getEnglishText());
                banProfile.setEvidence("Tried bypassing the ban of " + playerAcc.getPlayerName());
                banProfile.setAuthorId(Punish.getConsoleUuid());
                banProfile.setCreateDate(System.currentTimeMillis());

                bungeeCore.getCoreAPI().getBanService().saveEntity(banProfile, false, true);

                loginEvent.setCancelled(true);
                loginEvent.setCancelReason(BanUtil.generateBanScreen(banProfile));
                BungeeCore.getInstance().getBungeePlayerManager().notifyStaff(BanUtil.generateBanMessage(banProfile));
                return true;
            }

        }
        return false;
    }

}

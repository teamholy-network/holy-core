package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryProfile;
import de.teamholy.core.api.entities.staff.StaffProfile;
import de.teamholy.core.api.utility.PartyInviteAllowance;
import de.teamholy.core.api.utility.PlayerRank;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
public class PostLoginListener implements Listener {


    public PostLoginListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler(priority = 0)
    public void onLogin(PostLoginEvent loginEvent) {
        ProxiedPlayer proxiedPlayer = loginEvent.getPlayer();

        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(proxiedPlayer.getUniqueId(),
                () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));

        String ipAddress = proxiedPlayer.getAddress().getAddress().getHostAddress();

        if (playerProfile == null) {
            playerProfile = new PlayerProfile();
            playerProfile.setPlayerId(proxiedPlayer.getUniqueId());
            playerProfile.setPlayerName(proxiedPlayer.getName());
            playerProfile.setIp(ipAddress);
            playerProfile.setCoins(0L);
            playerProfile.setOnlineTime(0L);
            playerProfile.setRank(PlayerRank.PLAYER.getName());
            playerProfile.setStatsResetTokens(0L);
            playerProfile.setJoinMeTokens(0L);
            playerProfile.setCollectables(new HashMap<>());


            FriendProfile friendProfile = new FriendProfile();
            friendProfile.setPlayerId(proxiedPlayer.getUniqueId());
            friendProfile.setAllowFriendRequests(true);
            friendProfile.setPartyInviteAllowance(PartyInviteAllowance.EVERYONE);
            friendProfile.setAllowFriendJump(true);
            friendProfile.setFriendList(new ArrayList<>());
            friendProfile.setFriendReqeustsList(new ArrayList<>());

            PunishHistoryProfile punishHistoryProfile = new PunishHistoryProfile();
            punishHistoryProfile.setPlayerId(proxiedPlayer.getUniqueId());
            punishHistoryProfile.setBanProfileMap(new HashMap<>());
            punishHistoryProfile.setMuteProfileMap(new HashMap<>());

            GameProfile gameProfile = new GameProfile();
            gameProfile.setPlayerId(proxiedPlayer.getUniqueId());
            gameProfile.setSettingsMap(new HashMap<>());
            gameProfile.setStatsMap(new HashMap<>());

            PerkPlayerProfile perkPlayerProfile = new PerkPlayerProfile();
            perkPlayerProfile.setPlayerId(proxiedPlayer.getUniqueId());
            perkPlayerProfile.setBlockPerk(0);
            perkPlayerProfile.setStickPerk(100);
            perkPlayerProfile.setChatPerk(200);
            perkPlayerProfile.setOwnedPerks(new ArrayList<>());

            if (proxiedPlayer.hasPermission("teamholy.team")) {

                StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(proxiedPlayer.getUniqueId(),
                        () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));

                if (staffProfile == null)  {
                    staffProfile = new StaffProfile();
                    staffProfile.setPlayerId(proxiedPlayer.getUniqueId());
                    staffProfile.setNotify(true);
                    staffProfile.setBanProfileList(new ArrayList<>());
                    staffProfile.setMuteProfileList(new ArrayList<>());
                    staffProfile.setReportList(new ArrayList<>());
                    BungeeCore.getAPI().getStaffService().saveEntity(staffProfile,true,true);
                }

            }

            BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile,true,true);
            BungeeCore.getAPI().getFriendService().saveEntity(friendProfile,true,true);
            BungeeCore.getAPI().getGameService().saveEntity(gameProfile,true,true);
            BungeeCore.getAPI().getPunishHistoryService().saveEntity(punishHistoryProfile,true,true);
            BungeeCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile,true,true);
            return;
        }

        PlayerProfile finalPlayerProfile = playerProfile;
        BungeeCore.getAPI().getExecutor().execute(() -> {

            finalPlayerProfile.setOnline(true);
            if (!finalPlayerProfile.getPlayerName().equalsIgnoreCase(proxiedPlayer.getName())) {
                finalPlayerProfile.setPlayerName(proxiedPlayer.getName());
            }

            if (!finalPlayerProfile.getIp().equalsIgnoreCase(ipAddress)) {
                finalPlayerProfile.setIp(ipAddress);
            }

            IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(finalPlayerProfile.getPlayerId());
            String group = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName();
            if (!group.equalsIgnoreCase(finalPlayerProfile.getRank())) {
                finalPlayerProfile.setRank(group);
            }

            BungeeCore.getAPI().getPlayerService().saveEntity(finalPlayerProfile,true,true);

        });
    }

}

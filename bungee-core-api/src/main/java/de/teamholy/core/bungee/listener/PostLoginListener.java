package de.teamholy.core.bungee.listener;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;


/* copyright by Yassino & Gregorr */
public class PostLoginListener implements Listener {

    ProxyManager proxyManager;

    private final String[] blockedIsns = new String[]{"31163"};


    public PostLoginListener(ProxyManager proxyManager) {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
        this.proxyManager = proxyManager;
    }

    @EventHandler
    public void onLogin(PostLoginEvent loginEvent) {
        ProxiedPlayer proxiedPlayer = loginEvent.getPlayer();

        proxiedPlayer.sendMessage("");
        proxiedPlayer.sendMessage("          §f§lWELCOME ON §6§lTEAMHOLY          ");
        proxiedPlayer.sendMessage("        §7sponsored by §bIndex-Hosting.de      ");
        proxiedPlayer.sendMessage(" ");
        proxiedPlayer.sendMessage("§3Discord §8» §7https://discord.gg/teamholy");
        proxiedPlayer.sendMessage("§cStore §8» §7https://teamholy.de/shop");
        proxiedPlayer.sendMessage("§6Vote §8» §7https://teamholy.de/vote");
        proxiedPlayer.sendMessage("§5Website §8» §7https://teamholy.de");
        proxiedPlayer.sendMessage("");

        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));

        FriendProfile friendProfile;
        PunishHistoryProfile punishHistoryProfile;
        GameProfile gameProfile;
        PerkPlayerProfile perkPlayerProfile;


        String ipAddress = proxiedPlayer.getAddress().getAddress().getHostAddress();

        if (!proxiedPlayer.hasPermission("teamholy.joinfilter.bypass")) {
            BungeeCore.getAPI().getExecutor().execute(() -> {
                proxyManager.containsProxy(ipAddress).thenAccept(contains -> {
                    if (contains) {
                        proxiedPlayer.disconnect(proxyManager.kickMessage);
                        proxyManager.sendProxyWarning(proxiedPlayer, ipAddress);
                    } else {
                        proxyManager.checkProxy(ipAddress, (isProxy, countryName, org) -> {
                            if (isProxy) {
                                proxiedPlayer.sendMessage(proxyManager.kickMessage);
                                proxyManager.sendProxyWarning(proxiedPlayer, ipAddress);
                                proxyManager.addProxy(ipAddress);
                            } else {
                                proxyManager.removeProxy(ipAddress);
                            }
                        });
                    }
                });

                proxyManager.checkASN(ipAddress, asn -> {
                    if (asn != null) {
                        for (String blockedIsaEntry : blockedIsns) {
                            if (asn.contains(blockedIsaEntry)) {
                                proxiedPlayer.disconnect(proxyManager.kickMessage);
                                proxyManager.sendAsnWarning(proxiedPlayer, ipAddress, asn);
                                break;
                            }
                        }
                    }
                });


            });
        }


        boolean save = false;
        if (playerProfile == null) {
            save = true;
            playerProfile = new PlayerProfile();
            playerProfile.setPlayerId(proxiedPlayer.getUniqueId());
            playerProfile.setPlayerName(proxiedPlayer.getName());
            playerProfile.setIp(ipAddress);
            playerProfile.setCoins(0L);
            playerProfile.setOnlineTime(0L);
            playerProfile.setRank(PlayerRank.PLAYER.toString());
            playerProfile.setStatsResetTokens(0L);
            playerProfile.setJoinMeTokens(0L);
            playerProfile.setAutoNick(false);
            playerProfile.setCollectables(new HashMap<>());
            playerProfile.setFirstJoin(System.currentTimeMillis());
            playerProfile.setLastJoin(System.currentTimeMillis());


            friendProfile = new FriendProfile();
            friendProfile.setPlayerId(proxiedPlayer.getUniqueId());
            friendProfile.setAllowFriendRequests(true);
            friendProfile.setPartyInviteAllowance(PartyInviteAllowance.EVERYONE);
            friendProfile.setAllowFriendJump(true);
            friendProfile.setFriendList(new ArrayList<>());
            friendProfile.setFriendReqeustsList(new ArrayList<>());

            punishHistoryProfile = new PunishHistoryProfile();
            punishHistoryProfile.setPlayerId(proxiedPlayer.getUniqueId());
            punishHistoryProfile.setBanProfileMap(new HashMap<>());
            punishHistoryProfile.setMuteProfileMap(new HashMap<>());

            gameProfile = new GameProfile();
            gameProfile.setPlayerId(proxiedPlayer.getUniqueId());
            gameProfile.setSettingsMap(new HashMap<>());
            gameProfile.setStatsMap(new HashMap<>());

            perkPlayerProfile = new PerkPlayerProfile();
            perkPlayerProfile.setPlayerId(proxiedPlayer.getUniqueId());
            perkPlayerProfile.setBlockPerk(0);
            perkPlayerProfile.setStickPerk(100);
            perkPlayerProfile.setChatPerk(200);
            perkPlayerProfile.setOwnedPerks(new ArrayList<>());
            perkPlayerProfile.setCustomBanner(new CustomBanner());


        } else {
            friendProfile = BungeeCore.getAPI().getFriendService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
            punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
            gameProfile = BungeeCore.getAPI().getGameService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getGameService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
            perkPlayerProfile = BungeeCore.getAPI().getPerkPlayerService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getPerkPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));

            PlayerProfile finalPlayerProfile = playerProfile;
            BungeeCore.getAPI().getExecutor().execute(() -> {


                finalPlayerProfile.setOnline(true);
                finalPlayerProfile.setLastJoin(System.currentTimeMillis());
                if (!finalPlayerProfile.getPlayerName().equalsIgnoreCase(proxiedPlayer.getName())) {
                    finalPlayerProfile.setPlayerName(proxiedPlayer.getName());
                }

                if (!finalPlayerProfile.getIp().equalsIgnoreCase(ipAddress)) {
                    finalPlayerProfile.setIp(ipAddress);
                }

                if (!proxiedPlayer.hasPermission("markupapi.nick") && finalPlayerProfile.isAutoNick())
                    finalPlayerProfile.setAutoNick(false);

                IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(finalPlayerProfile.getPlayerId());
                String group = CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName();
                if (!group.equalsIgnoreCase(finalPlayerProfile.getRank())) {
                    finalPlayerProfile.setRank(group.toUpperCase(Locale.ROOT));
                }


                BungeeCore.getAPI().getPlayerService().saveEntity(finalPlayerProfile, true, true);
            });
        }

        if (proxiedPlayer.hasPermission("teamholy.team")) {
            StaffProfile staffProfile = BungeeCore.getAPI().getStaffService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getStaffService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
            boolean updateStaffDB = false;
            if (staffProfile == null) {
                updateStaffDB = true;
                staffProfile = new StaffProfile();
                staffProfile.setPlayerId(proxiedPlayer.getUniqueId());
                staffProfile.setNotify(true);
                staffProfile.setBanProfileList(new ArrayList<>());
                staffProfile.setMuteProfileList(new ArrayList<>());
                staffProfile.setReportList(new ArrayList<>());
            }

            BungeeCore.getAPI().getStaffService().saveEntity(staffProfile, true, updateStaffDB);
            BungeeCore.getInstance().getBungeePlayerManager().notifyStaff("§cTeam §8× " + BungeeCore.getAPI().getCloudManager().getColor(proxiedPlayer.getUniqueId()) + proxiedPlayer.getName() + " §7is now §aonline");
        }


        int i = 0;
        StringBuilder onlineFriends = new StringBuilder();
        String name = BungeeCore.getAPI().getCloudManager().getColor(proxiedPlayer.getUniqueId()) + proxiedPlayer.getName();
        BungeeCore.getAPI().getFriendManager().sendFriendUpdateData(proxiedPlayer.getUniqueId(), null, "online", null);
        for (UUID uuid : friendProfile.getFriendList()) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
            if (target != null) {
                i++;
                target.sendMessage("§6Friend §8× §7Your friend " + name + " §7is now §aonline");
                onlineFriends.append(BungeeCore.getAPI().getCloudManager().getColor(target.getUniqueId()) + target.getName()).append("§7, ");
            }
        }

        proxiedPlayer.sendMessage("§6Friend §8× §7There " + (i <= 1 ? "is" : "are") + " currently " + (i == 0 ? "§cno §7friend" : "§a" + i + " §7friend" + (i >= 2 ? "s" : "")) + " online");

        if (i > 0) {
            onlineFriends.setLength(onlineFriends.length() - 2);
            proxiedPlayer.sendMessage("§6Friend §8× " + onlineFriends);
        }


        int sizeofRequests = friendProfile.getFriendReqeustsList().size();

        if (sizeofRequests == 1) {
            proxiedPlayer.sendMessage(new ComponentBuilder("§6Friend §8× §7You currently have §a" + sizeofRequests + " §7open friend request").append(" §8(§a§lCLICK§8)").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend requests")).create());
        } else if (sizeofRequests != 0) {
            proxiedPlayer.sendMessage(new ComponentBuilder("§6Friend §8× §7You currently have §a" + sizeofRequests + " §7open friend requests").append(" §8(§a§lCLICK§8)").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend requests")).create());
        }


        ClanPlayerProfile clanPlayerProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(proxiedPlayer.getUniqueId(), () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));

        BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile, true, save);

        if (clanPlayerProfile != null) {
            BungeeCore.getAPI().getClanPlayerService().saveEntity(clanPlayerProfile, true, save);
        }

        if (friendProfile != null) {
            BungeeCore.getAPI().getFriendService().saveEntity(friendProfile, true, save);
        }

        if (punishHistoryProfile != null) {
            BungeeCore.getAPI().getPunishHistoryService().saveEntity(punishHistoryProfile, true, save);
        }

        if (gameProfile != null) {
            BungeeCore.getAPI().getGameService().saveEntity(gameProfile, true, save);
        }

        if (perkPlayerProfile != null) {
            BungeeCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile, true, save);
        }

        BungeeCore.getAPI().getReportManager().getAllReports().values().forEach(report -> {
            if (report.getTarget().equals(proxiedPlayer.getUniqueId())) {
                report.setTargetOnline(true);
                BungeeCore.getAPI().getReportManager().addReport(report);
            }
        });



    }

}

package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.entities.clan.Clan;
import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/* copyright by Yassino */
public class PostDisconnectListener implements Listener {

    public PostDisconnectListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }


    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));



        playerProfile.setOnline(false);
        BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile,false,true);

        ClanPlayerProfile clanPlayerProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(player.getUniqueId()));

        if (clanPlayerProfile == null) return;
        Clan clan = BungeeCore.getAPI().getClanManager().getClanById(clanPlayerProfile.getClanId());

        boolean allOffline = true;

        for (UUID member : clan.getMembers()) {
            if (BungeeCore.getInstance().getBungeePlayerManager().isOnline(member)) {
                allOffline = false;
                break;
            }
        }

        if (allOffline) BungeeCore.getAPI().getClanManager().unforce(clanPlayerProfile.getClanId());



        BungeeCore.getAPI().getStaffService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getClanPlayerService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getFriendService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getSkinService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getBanService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getMuteService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getGameService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getPunishHistoryService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getPerkPlayerService().getRedisCache().updateEntryExpiration(player.getUniqueId(),15, TimeUnit.MINUTES,0,TimeUnit.SECONDS);
        BungeeCore.getAPI().getNickManager().removeNick(player.getUniqueId());

    }

}

package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.entities.clanplayer.ClanPlayerProfile;
import de.teamholy.core.api.entities.friend.FriendProfile;
import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.game.GameProfile;
import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import de.teamholy.core.api.entities.punishhistory.PunishHistoryProfile;
import de.teamholy.core.api.entities.skin.SkinProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/* copyright by Yassino */
public class CacheListener implements Listener {

    public CacheListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }


    @EventHandler(priority = 64)
    public void onLogin(PostLoginEvent loginEvent) {
        ProxiedPlayer proxiedPlayer = loginEvent.getPlayer();

        SkinProfile skinProfile = BungeeCore.getAPI().getSkinService().getEntity(proxiedPlayer.getUniqueId(),() -> BungeeCore.getAPI().getSkinService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
        ClanPlayerProfile clanPlayerProfile = BungeeCore.getAPI().getClanPlayerService().getEntity(proxiedPlayer.getUniqueId(),() -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
        FriendProfile friendProfile = BungeeCore.getAPI().getFriendService().getEntity(proxiedPlayer.getUniqueId(),() -> BungeeCore.getAPI().getFriendService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
        PunishHistoryProfile punishHistoryProfile = BungeeCore.getAPI().getPunishHistoryService().getEntity(proxiedPlayer.getUniqueId(),() -> BungeeCore.getAPI().getPunishHistoryService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
        GameProfile gameProfile = BungeeCore.getAPI().getGameService().getEntity(proxiedPlayer.getUniqueId(),() -> BungeeCore.getAPI().getGameService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));
        PerkPlayerProfile perkPlayerProfile = BungeeCore.getAPI().getPerkPlayerService().getEntity(proxiedPlayer.getUniqueId(),() -> BungeeCore.getAPI().getPerkPlayerService().getRepository().findFirstById(proxiedPlayer.getUniqueId()));

        if (skinProfile != null) {
            BungeeCore.getAPI().getSkinService().saveEntity(skinProfile,true,false);
        }

        if (clanPlayerProfile != null) {
            BungeeCore.getAPI().getClanPlayerService().saveEntity(clanPlayerProfile,true,false);
        }

        if (friendProfile != null) {
            BungeeCore.getAPI().getFriendService().saveEntity(friendProfile,true,false);
        }

        if (punishHistoryProfile != null) {
            BungeeCore.getAPI().getPunishHistoryService().saveEntity(punishHistoryProfile,true,false);
        }

        if (gameProfile != null) {
            BungeeCore.getAPI().getGameService().saveEntity(gameProfile,true,false);
        }

        if (perkPlayerProfile != null) {
            BungeeCore.getAPI().getPerkPlayerService().saveEntity(perkPlayerProfile,true,false);
        }

    }


}

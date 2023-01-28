package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.entities.player.PlayerProfile;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/* copyright by Yassino */
public class ServerConnectedListener implements Listener {

    public ServerConnectedListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PlayerProfile playerProfile = BungeeCore.getAPI().getPlayerService().getEntity(player.getUniqueId(),
                () -> BungeeCore.getAPI().getPlayerService().getRepository().findFirstById(player.getUniqueId()));
        playerProfile.setServerName(event.getServer().getInfo().getName());
        BungeeCore.getAPI().getPlayerService().saveEntity(playerProfile,true,true);
    }

}

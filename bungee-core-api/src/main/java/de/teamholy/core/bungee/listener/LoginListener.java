package de.teamholy.core.bungee.listener;

import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/* copyright by Yassino */
public class LoginListener implements Listener {

    public LoginListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onJoin(LoginEvent loginEvent) {

        UUID uuid = loginEvent.getConnection().getUniqueId();
        String name = loginEvent.getConnection().getName();

        BungeeCore.getAPI().getUuidManager().register(name, uuid);

        BungeeCore.getAPI().getClanPlayerService().getEntityAsync(uuid,
            () -> BungeeCore.getAPI().getClanPlayerService().getRepository().findFirstById(uuid), clanPlayerProfile -> {

                if (clanPlayerProfile == null) return;
                if (!BungeeCore.getAPI().getClanManager().loadAndForce(uuid, clanPlayerProfile.getClanId())) {
                    BungeeCore.getAPI().getClanPlayerService().deleteEntity(clanPlayerProfile);
                }
            });

    }


}

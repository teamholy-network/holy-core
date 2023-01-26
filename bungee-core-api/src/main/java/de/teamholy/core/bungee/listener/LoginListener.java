package de.teamholy.core.bungee.listener;

import de.teamholy.core.api.CoreAPI;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/* copyright by Yassino */
public class LoginListener implements Listener {

    public LoginListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(),this);
    }


    @EventHandler
    public void onJoin(LoginEvent loginEvent) {
        
    }

}

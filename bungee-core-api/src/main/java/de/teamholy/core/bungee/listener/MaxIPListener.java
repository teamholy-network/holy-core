package de.teamholy.core.bungee.listener;

import gnu.trove.TCollections;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetAddress;

/* copyright by Yassino */
public class MaxIPListener implements Listener {

    private final TObjectIntMap<InetAddress> addresses = TCollections.synchronizedMap(new TObjectIntHashMap());


    @EventHandler
    public void login(LoginEvent event) {
        if (this.addresses.get(event.getConnection().getAddress().getAddress()) >= 3) {
            event.setCancelReason("§c§lTo many players with the same §6§lIP");
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = -126)
    public void postLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        this.addresses.adjustOrPutValue(event.getPlayer().getAddress().getAddress(), 1, 1);
    }

    @EventHandler
    public void disconnect(PlayerDisconnectEvent event) {
        InetAddress addr = event.getPlayer().getAddress().getAddress();

        this.addresses.adjustValue(addr, -1);
        if (this.addresses.get(addr) <= 0)
            this.addresses.remove(addr);
    }


}

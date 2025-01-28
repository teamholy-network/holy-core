package de.teamholy.core.bungee.listener;

import de.skydb.translateapi.bindings.BungeeTranslateAPI;
import de.teamholy.core.bungee.BungeeCore;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class PlayerListListener implements Listener {

    /**
     * Updates the player's tab header and footer with translated placeholders.
     */
    private void updateTabList(ProxiedPlayer player) {
        int onlineCount = ProxyServer.getInstance().getOnlineCount();
        String server = player.getServer().getInfo().getName();


        String HEADER_TEMPLATE = "\n§6§lTeamHoly.de§r§8 ┃ §f" + onlineCount + " §7" + BungeeTranslateAPI.translate(player, "players") + "\n§7" + BungeeTranslateAPI.translatePlaceholder(player, "You are on {}", "§e" + server) + "\n ";
        String FOOTER_TEMPLATE = "\n    §7§o" + BungeeTranslateAPI.translate(player, "Use these commands for help:") + "    \n§f/discord §8┃ §f/shop §8┃ §f/report\n\n§7§o"; /* +BungeeTranslateAPI.translatePlaceholder(player, "sponsored by {}", "§b§oIndex-Hosting.de")*/

        // Set the header and footer
        player.setTabHeader(TextComponent.fromLegacyText(HEADER_TEMPLATE), TextComponent.fromLegacyText(FOOTER_TEMPLATE));
    }

    /**
     * Event: Triggered when a player logs in.
     */
    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(BungeeCore.getInstance(), () -> ProxyServer.getInstance().getPlayers().forEach(this::updateTabList), 1, TimeUnit.SECONDS);
    }

    /**
     * Event: Triggered when a player logs in.
     */
    @EventHandler
    public void onPostLogin(PlayerDisconnectEvent event) {
        ProxyServer.getInstance().getPlayers().forEach(this::updateTabList);
    }

    /**
     * Event: Triggered when a player switches servers.
     */
    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        updateTabList(event.getPlayer());
    }
}

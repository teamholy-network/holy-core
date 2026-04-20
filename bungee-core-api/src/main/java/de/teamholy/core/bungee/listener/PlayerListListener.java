package de.teamholy.core.bungee.listener;

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

/**
 * PlayerListListener is a listener class responsible for managing and updating
 * the player tab list header and footer dynamically in a BungeeCord proxy server.
 * It listens to key player events such as login, disconnect, and server switch,
 * and dynamically updates the information displayed in the tab list for all players.
 */
public class PlayerListListener implements Listener {

    private void updateTabList(ProxiedPlayer player) {
        if (player == null || player.getServer() == null) {
            return;
        }

        int onlineCount = ProxyServer.getInstance().getOnlineCount();
        String serverName = player.getServer().getInfo().getName();

        String header = buildHeader(player, onlineCount, serverName);
        String footer = buildFooter(player);

        player.setTabHeader(
            new TextComponent(header),
            new TextComponent(footer)
        );
    }

    private String buildHeader(ProxiedPlayer player, int onlineCount, String serverName) {
        return "\n§6§lTeamHoly.de§r§8 ┃ §f" + onlineCount + " §7"
            + "players"
            + "\n§7" + ("You are on " + ("§e" + serverName))
            + "\n ";
    }

    private String buildFooter(ProxiedPlayer player) {
        return "\n    §7§o" + "Use these commands for help:"
            + "    \n§f/discord §8┃ §f/shop §8┃ §f/report\n\n§7§o";
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(
            BungeeCore.getInstance(),
            () -> ProxyServer.getInstance().getPlayers().forEach(this::updateTabList),
            1,
            TimeUnit.SECONDS
        );
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxyServer.getInstance().getPlayers().forEach(this::updateTabList);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        updateTabList(event.getPlayer());
    }
}
package de.teamholy.core.bungee.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class TabCompleteListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer sender)) {
            return;
        }

        if (!sender.hasPermission("teamholy.team")) {
            return;
        }

        String partialPlayerName = event.getCursor().toLowerCase();

        int lastSpaceIndex = partialPlayerName.lastIndexOf(' ');
        if (lastSpaceIndex >= 0) {
            partialPlayerName = partialPlayerName.substring(lastSpaceIndex + 1);
        }

        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (p.getName().toLowerCase().startsWith(partialPlayerName)) {
                event.getSuggestions().add(p.getName());
            }
        }
    }
}

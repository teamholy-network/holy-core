package de.teamholy.core.bungee.listener;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.ChatLogManager;
import de.teamholy.core.bungee.util.BanUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * The ChatLogListener class is responsible for handling chat events in the server and logging relevant messages.
 * It listens to chat events and logs messages into the chat log system managed by {@link ChatLogManager}.
 * It filters and processes messages sent by players and avoids logging messages that meet specific skip conditions.
 */
public class ChatLogListener implements Listener {

    private final ChatLogManager chatLogManager;

    public ChatLogListener() {
        this.chatLogManager = BungeeCore.getInstance().getChatLogManager();
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer player)) {
            return;
        }

        String message = event.getMessage();

        if (shouldSkipMessage(message)) {
            return;
        }

        logMessage(player, message);
    }

    private boolean shouldSkipMessage(String message) {
        return message.startsWith("/") && !BanUtil.isFilteredCommand(message);
    }

    private void logMessage(ProxiedPlayer player, String message) {
        UUID playerId = player.getUniqueId();
        String serverName = player.getServer().getInfo().getName();
        long timestamp = System.currentTimeMillis();

        ChatLogManager.Message logMessage = new ChatLogManager.Message(message, serverName, timestamp);
        chatLogManager.addMessageToChatlog(playerId, logMessage);
    }
}
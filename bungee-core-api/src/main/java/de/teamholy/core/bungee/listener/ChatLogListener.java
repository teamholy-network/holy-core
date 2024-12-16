package de.teamholy.core.bungee.listener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.manager.ChatLogManager;
import de.teamholy.core.bungee.manager.LensRedisManager;
import de.teamholy.core.bungee.model.ChatLog;
import de.teamholy.core.bungee.util.BanUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Type;
import java.util.*;

public class ChatLogListener implements Listener {

    private ChatLogManager chatLogManager = BungeeCore.getInstance().getChatLogManager();

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer player)) return;

        UUID playerUUID = player.getUniqueId();
        String message = event.getMessage();
        String serverName = player.getServer().getInfo().getName();

        if (message.startsWith("/") && !BanUtil.isFilteredCommand(message)) return;

        chatLogManager.addMessageToChatlog(playerUUID, new ChatLogManager.Message(message, serverName, System.currentTimeMillis()));
    }


}

package de.teamholy.core.bungee.listener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.model.ChatLog;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Type;
import java.util.*;

public class ChatLogListener implements Listener {



    public static Map<UUID, LinkedList<Message>> CHATLOGS = new HashMap<>();
    private static final int max = 50;
    private final Gson gson = new Gson();
    private final Type typeOfSrc = new TypeToken<List<Message>>(){}.getType();

    public ChatLogListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeCore.getInstance(), this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer player) {

            UUID playerUUID = player.getUniqueId();
            String message = event.getMessage();
            String serverName = player.getServer().getInfo().getName();

            if (message.startsWith("/")) {
                return;
            }

            LinkedList<Message> playerChatLog = CHATLOGS.getOrDefault(playerUUID, new LinkedList<>());

            playerChatLog.add(new Message(message, serverName));


            if (playerChatLog.size() > max) {
                playerChatLog.removeFirst();
            }


            CHATLOGS.put(playerUUID, playerChatLog);

        }
    }

    public String getChatLog(UUID uuid) {
        LinkedList<Message> chatLog = CHATLOGS.get(uuid);
        if (chatLog != null) {
            return gson.toJson(chatLog, typeOfSrc);
        } else {
            return null;
        }
    }

    private static class Message {
        String message;

        String server;
        long time;



        Message(String message, String server) {
            this.message = message;
            this.server = server;
            this.time = System.currentTimeMillis();
        }
    }








}

package de.teamholy.core.bungee.manager;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.model.ChatLog;
import de.teamholy.core.bungee.repositories.ChatLogRepository;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.*;

public class ChatLogManager {
    @Getter
    private ChatLogRepository chatLogRepository;

    public static final Map<UUID, LinkedList<Message>> CHATLOGS = new HashMap<>();
    private static final SecureRandom RND = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int max = 50;
    private final Gson gson = new Gson();


    private final Type typeOfSrc = new TypeToken<List<Message>>() {
    }.getType();

    public ChatLogManager() {
        chatLogRepository = BungeeCore.getAPI().getMongoManager().create(ChatLogRepository.class);
    }

    public ChatLog createChatlog(UUID requestUUID, ProxiedPlayer chatlogPlayer) {

        if (chatlogPlayer == null) return null;


        if (CHATLOGS.getOrDefault(chatlogPlayer.getUniqueId(), new LinkedList<>()).isEmpty()) {
            return null;
        }


        String chatlogString = getChatLogToString(chatlogPlayer.getUniqueId());


        String id;
        do {
            id = generateRandomKey();
        } while (chatLogRepository.existsById(id));

        List<Message> chatLogMessages = new Gson().fromJson(chatlogString, typeOfSrc);

        ChatLog chatLog = new ChatLog();
        chatLog.setChatLogId(id);
        chatLog.setChatLogCreated(System.currentTimeMillis());
        chatLog.setLoggedPlayerName(chatlogPlayer.getName());
        chatLog.setLoggedPlayerUUID(chatlogPlayer.getUniqueId());
        chatLog.setRequestPlayerName(Punish.getConsoleUuid() == requestUUID ? "CONSOLE" : BungeeCore.getAPI().getUuidManager().getName(requestUUID));
        chatLog.setRequestPlayerUUID(requestUUID);
        chatLog.setMessages(chatLogMessages);

        chatLogRepository.save(chatLog);

        return chatLog;
    }


    public String getChatLogToString(UUID uuid) {
        LinkedList<Message> chatLog = CHATLOGS.get(uuid);
        if (chatLog != null) {
            return gson.toJson(chatLog, typeOfSrc);
        } else {
            return null;
        }
    }

    public void addMessageToChatlog(UUID uuid, Message Message) {
        LinkedList<Message> chatLog = CHATLOGS.get(uuid);
        if (chatLog == null) {
            chatLog = new LinkedList<>();
        }
        chatLog.add(Message);
        if (chatLog.size() > max) {
            chatLog.removeFirst();
        }
        CHATLOGS.put(uuid, chatLog);
    }

    private String generateRandomKey() {
        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            sb.append(CHARS.charAt(RND.nextInt(CHARS.length())));
        }
        return sb.toString();
    }


    public record Message(String message, String server, long time) {
    }


}

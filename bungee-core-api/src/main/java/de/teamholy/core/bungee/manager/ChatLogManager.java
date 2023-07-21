package de.teamholy.core.bungee.manager;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.teamholy.core.api.utility.Punish;
import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.listener.ChatLogListener;
import de.teamholy.core.bungee.model.ChatLog;
import de.teamholy.core.bungee.repositories.ChatLogRepository;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ChatLogManager {
    @Getter
    private ChatLogRepository chatLogRepository;

    public ChatLogManager() {
        chatLogRepository = BungeeCore.getAPI().getMongoManager().create(ChatLogRepository.class);
    }

    public ChatLog createChatlog(UUID requestUUID, ProxiedPlayer chatlogPlayer) {

        if (ChatLogListener.CHATLOGS.get(chatlogPlayer.getUniqueId()).isEmpty()) {
            return null;
        }

        String chatlogString = new ChatLogListener().getChatLog(chatlogPlayer.getUniqueId());

        String randomKey = generateRandomKey();

        Type listType = new TypeToken<List<ChatLog.Message>>(){}.getType();
        List<ChatLog.Message> chatLogMessages = new Gson().fromJson(chatlogString, listType);

        ChatLog chatLog = new ChatLog();
        chatLog.setChatLogId(randomKey);
        chatLog.setChatLogCreated(System.currentTimeMillis());
        chatLog.setLoggedPlayerName(chatlogPlayer.getName());
        chatLog.setLoggedPlayerUUID(chatlogPlayer.getUniqueId());
        chatLog.setRequestPlayerName(Punish.getConsoleUuid() == requestUUID ? "CONSOLE" : BungeeCore.getAPI().getUuidManager().getName(requestUUID));
        chatLog.setRequestPlayerUUID(requestUUID);
        chatLog.setMessages(chatLogMessages);

        chatLogRepository.save(chatLog);

        return chatLog;
    }

    private String generateRandomKey() {
        Random random = new Random();
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 2; i++) {
            int character = random.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }

        builder.insert(0, "h");
        builder.insert(3, "oly");

        for (int i = 0; i < 4; i++) {
            int character = random.nextInt(ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }

        builder.append("chatlog");

        return builder.toString();
    }







}

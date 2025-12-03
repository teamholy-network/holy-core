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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the chat logs of players, including functionalities for storing,
 * retrieving, and manipulating chat logs in memory and database.
 *
 * Chat logs are tracked for individual players and can be managed through various
 * operations such as adding messages, clearing logs, and persisting logs to the repository.
 */
public class ChatLogManager {

    private static final int MAX_MESSAGES_PER_PLAYER = 50;
    private static final int CHATLOG_ID_LENGTH = 5;
    private static final String ID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static final Map<UUID, LinkedList<Message>> CHATLOGS = new ConcurrentHashMap<>();

    @Getter
    private final ChatLogRepository chatLogRepository;
    private final Gson gson;
    private final Type messageListType;
    private final Logger logger;

    public ChatLogManager() {
        this.chatLogRepository = BungeeCore.getAPI().getMongoManager().create(ChatLogRepository.class);
        this.gson = new Gson();
        this.messageListType = new TypeToken<List<Message>>() {
        }.getType();
        this.logger = Logger.getLogger(ChatLogManager.class.getName());
    }

    public ChatLog createChatlog(UUID requesterId, ProxiedPlayer targetPlayer) {
        if (targetPlayer == null) {
            logger.warning("Cannot create chatlog: target player is null");
            return null;
        }

        LinkedList<Message> messages = getPlayerMessages(targetPlayer.getUniqueId());

        if (messages.isEmpty()) {
            logger.info("Cannot create chatlog for " + targetPlayer.getName() + ": no messages found");
            return null;
        }

        try {
            String chatlogId = generateUniqueChatlogId();
            ChatLog chatLog = buildChatLog(chatlogId, requesterId, targetPlayer, messages);

            chatLogRepository.save(chatLog);

            logger.info("Created chatlog " + chatlogId + " for " + targetPlayer.getName() +
                " requested by " + getRequesterName(requesterId));

            return chatLog;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create chatlog for " + targetPlayer.getName(), e);
            return null;
        }
    }

    private ChatLog buildChatLog(String id, UUID requesterId, ProxiedPlayer targetPlayer,
                                 LinkedList<Message> messages) {
        ChatLog chatLog = new ChatLog();
        chatLog.setChatLogId(id);
        chatLog.setChatLogCreated(System.currentTimeMillis());
        chatLog.setLoggedPlayerName(targetPlayer.getName());
        chatLog.setLoggedPlayerUUID(targetPlayer.getUniqueId());
        chatLog.setRequestPlayerName(getRequesterName(requesterId));
        chatLog.setRequestPlayerUUID(requesterId);
        chatLog.setMessages(new ArrayList<>(messages));

        return chatLog;
    }

    private String getRequesterName(UUID requesterId) {
        if (requesterId.equals(Punish.getConsoleUuid())) {
            return "CONSOLE";
        }
        return BungeeCore.getAPI().getUuidManager().getName(requesterId);
    }

    private String generateUniqueChatlogId() {
        String id;
        int attempts = 0;
        final int maxAttempts = 100;

        do {
            id = generateRandomKey();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new IllegalStateException("Failed to generate unique chatlog ID after " + maxAttempts + " attempts");
            }
        } while (chatLogRepository.existsById(id));

        return id;
    }

    private String generateRandomKey() {
        StringBuilder sb = new StringBuilder(CHATLOG_ID_LENGTH);
        for (int i = 0; i < CHATLOG_ID_LENGTH; i++) {
            sb.append(ID_CHARACTERS.charAt(RANDOM.nextInt(ID_CHARACTERS.length())));
        }
        return sb.toString();
    }

    public void addMessageToChatlog(UUID playerId, Message message) {
        CHATLOGS.compute(playerId, (key, chatLog) -> {
            if (chatLog == null) {
                chatLog = new LinkedList<>();
            }

            chatLog.add(message);

            while (chatLog.size() > MAX_MESSAGES_PER_PLAYER) {
                chatLog.removeFirst();
            }

            return chatLog;
        });
    }

    public LinkedList<Message> getPlayerMessages(UUID playerId) {
        return CHATLOGS.getOrDefault(playerId, new LinkedList<>());
    }

    public String getChatLogToString(UUID playerId) {
        LinkedList<Message> chatLog = CHATLOGS.get(playerId);

        if (chatLog == null || chatLog.isEmpty()) {
            return null;
        }

        try {
            return gson.toJson(chatLog, messageListType);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to serialize chatlog for player " + playerId, e);
            return null;
        }
    }

    public boolean hasMessages(UUID playerId) {
        LinkedList<Message> messages = CHATLOGS.get(playerId);
        return messages != null && !messages.isEmpty();
    }

    public int getMessageCount(UUID playerId) {
        LinkedList<Message> messages = CHATLOGS.get(playerId);
        return messages != null ? messages.size() : 0;
    }

    public void clearPlayerMessages(UUID playerId) {
        CHATLOGS.remove(playerId);
    }

    public void clearAllChatlogs() {
        CHATLOGS.clear();
        logger.info("Cleared all in-memory chatlogs");
    }

    public int getTrackedPlayerCount() {
        return CHATLOGS.size();
    }

    public void cleanupOfflinePlayerLogs() {
        int removed = 0;
        Iterator<UUID> iterator = CHATLOGS.keySet().iterator();

        while (iterator.hasNext()) {
            UUID playerId = iterator.next();
            if (BungeeCore.getInstance().getProxy().getPlayer(playerId) == null) {
                iterator.remove();
                removed++;
            }
        }

        if (removed > 0) {
            logger.info("Cleaned up " + removed + " offline player chatlogs");
        }
    }

    public ChatLog getChatLogById(String chatlogId) {
        try {
            return chatLogRepository.findFirstById(chatlogId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to retrieve chatlog: " + chatlogId, e);
            return null;
        }
    }

    public record Message(String message, String server, long time) {

        public static Message now(String message, String server) {
            return new Message(message, server, System.currentTimeMillis());
        }

        public String getFormattedTime() {
            return new java.text.SimpleDateFormat("HH:mm:ss").format(new Date(time));
        }
    }
}
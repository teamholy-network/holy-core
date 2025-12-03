package de.teamholy.core.bungee.manager;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.listener.ChatFilterListener;
import de.teamholy.core.bungee.repositories.ChatFilterRepository;

import java.util.HashMap;

/**
 * The ChatFilterManager class is responsible for managing filtered words and their associated actions
 * within a chat filtering system. It interacts with a repository to load and store chat filter configurations.
 */
public class ChatFilterManager {

    public static final HashMap<String, FilterActionProfile> FILTEREDWORDS = new HashMap<>();

    private final ChatFilterRepository chatFilterRepository;

    public ChatFilterManager() {
        this.chatFilterRepository = BungeeCore.getAPI().getMongoManager().create(ChatFilterRepository.class);
    }

    public void loadFilteredWords() {
        FILTEREDWORDS.clear();
        chatFilterRepository.findAll().forEach(chatFilter ->
            FILTEREDWORDS.put(chatFilter.getWord(),
                new FilterActionProfile(chatFilter.getFilterAction(), chatFilter.getFilterActionId()))
        );
    }

    public void clearLastMessages() {
        ChatFilterListener.LASTMESSAGES.clear();
    }

    public record FilterActionProfile(String filterAction, Integer filterActionId) {
    }
}
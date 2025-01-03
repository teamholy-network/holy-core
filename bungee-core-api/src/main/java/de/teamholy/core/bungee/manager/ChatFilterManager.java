package de.teamholy.core.bungee.manager;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.repositories.ChatFilterRepository;

import java.util.HashMap;

/* copyright by Greg */

public class ChatFilterManager {

    public static final HashMap<String, FilterActionProfile> FILTEREDWORDS = new HashMap<>();
    private ChatFilterRepository chatFilterRepository;

    public ChatFilterManager() {
        chatFilterRepository = BungeeCore.getAPI().getMongoManager().create(ChatFilterRepository.class);
    }

    public void loadFilteredWords() {
        FILTEREDWORDS.clear();
        chatFilterRepository.findAll().forEach(chatFilter -> FILTEREDWORDS.put(chatFilter.getWord(), new FilterActionProfile(chatFilter.getFilterAction(), chatFilter.getFilterActionId())));
    }

    public record FilterActionProfile(String filterAction, Integer filterActionId) {
    }

}



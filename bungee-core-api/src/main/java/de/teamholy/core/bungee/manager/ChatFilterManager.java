package de.teamholy.core.bungee.manager;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.repositories.ChatFilterRepository;
import lombok.Getter;


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
        System.out.println("loaded words from filter:");
        FILTEREDWORDS.forEach((s, filterActionProfile) -> System.out.println(s + " action: " + filterActionProfile.filterAction() + " action ID: " + filterActionProfile.filterActionId()));
    }

    public record FilterActionProfile(String filterAction, Integer filterActionId) {}

}



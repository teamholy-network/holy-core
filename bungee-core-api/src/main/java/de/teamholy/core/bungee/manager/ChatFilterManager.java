package de.teamholy.core.bungee.manager;

import de.teamholy.core.bungee.BungeeCore;
import de.teamholy.core.bungee.repositories.ChatFilterRepository;


import java.util.HashMap;

/* copyright by Greg */

public class ChatFilterManager {

    public static final HashMap<String, Integer> FILTEREDWORDS = new HashMap<>();
    private ChatFilterRepository chatFilterRepository;

    public ChatFilterManager() {
        chatFilterRepository = BungeeCore.getAPI().getMongoManager().create(ChatFilterRepository.class);
    }

    public void loadFilteredWords() {
        FILTEREDWORDS.clear();
        chatFilterRepository.findAll().forEach(chatFilter -> FILTEREDWORDS.put(chatFilter.getWord(), chatFilter.getSeverity()));
        System.out.println("loaded words from filter:");
        FILTEREDWORDS.forEach((s, integer) -> System.out.println(s + " severity: " + integer));

    }






}

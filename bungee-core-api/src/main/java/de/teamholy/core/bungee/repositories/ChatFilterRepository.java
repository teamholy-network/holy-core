package de.teamholy.core.bungee.repositories;

import de.teamholy.core.bungee.model.ChatFilter;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

@Collection("chatfilter_collection")
public interface ChatFilterRepository extends Repository<ChatFilter, String> {
}

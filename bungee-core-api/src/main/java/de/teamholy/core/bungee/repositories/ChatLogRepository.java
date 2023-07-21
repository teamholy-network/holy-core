package de.teamholy.core.bungee.repositories;

import de.teamholy.core.bungee.model.ChatLog;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

@Collection("chatlog_collection")

public interface ChatLogRepository extends Repository<ChatLog, String> {
}
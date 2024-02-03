package de.teamholy.core.bungee.model;

import de.teamholy.core.bungee.manager.ChatLogManager;
import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatLog {

    @Id
    private String chatLogId;
    private long chatLogCreated;

    private String loggedPlayerName;
    private UUID loggedPlayerUUID;

    private String requestPlayerName;
    private UUID requestPlayerUUID;

    private List<ChatLogManager.Message> messages;

}

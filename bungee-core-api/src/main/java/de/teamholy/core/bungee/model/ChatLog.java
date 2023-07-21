package de.teamholy.core.bungee.model;

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

    private List<Message> messages;

    @Getter
    @Setter
    public static class Message {
        private String message;
        private String server;
        private long time;
    }
}

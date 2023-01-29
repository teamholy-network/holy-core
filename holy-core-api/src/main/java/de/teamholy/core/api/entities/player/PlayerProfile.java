package de.teamholy.core.api.entities.player;

import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PlayerProfile {

    @Id
    UUID playerId;

    String playerName;
    String ip;
    String serverName;
    boolean online;
    long coins;
    String rank;
    long onlineTime;
    long joinMeTokens;
    long statsResetTokens;
}
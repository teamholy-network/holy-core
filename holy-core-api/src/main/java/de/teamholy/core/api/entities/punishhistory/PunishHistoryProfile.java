package de.teamholy.core.api.entities.punishhistory;

import de.teamholy.core.api.entities.ban.BanProfile;
import de.teamholy.core.api.entities.mute.MuteProfile;
import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PunishHistoryProfile {

    @Id
    UUID playerId;

    Map<String, BanProfile> banProfileMap = new HashMap<>();
    Map<String, MuteProfile> muteProfileMap = new HashMap<>();
}
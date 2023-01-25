package de.teamholy.core.api.entities.stats;

import de.teamholy.core.api.entities.punish.PunishType;
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
public class StatsProfile {

    @Id
    UUID playerId;

    Map<StatsType, Integer> kills = new HashMap<>();
    Map<StatsType, Integer> deaths = new HashMap<>();
}
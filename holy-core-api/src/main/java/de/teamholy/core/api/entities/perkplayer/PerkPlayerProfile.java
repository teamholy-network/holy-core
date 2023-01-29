package de.teamholy.core.api.entities.perkplayer;

import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class PerkPlayerProfile {

    @Id
    UUID playerId;

    Integer stickPerk;
    Integer blockPerk;
    Integer chatPerk;
    List<Integer> ownedPerks;

}
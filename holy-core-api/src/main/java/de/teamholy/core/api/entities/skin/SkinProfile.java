package de.teamholy.core.api.entities.skin;

import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class SkinProfile {

    @Id
    UUID playerId;

    String value;
    String texture;

}
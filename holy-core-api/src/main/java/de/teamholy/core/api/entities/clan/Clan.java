package de.teamholy.core.api.entities.clan;

import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Clan {

    @Id
    private UUID clanId;

    private String name;
    private String tag;
    private String color = "§e";
    private boolean openClan = false;
    private List<UUID> members;
    private List<UUID> requestsTo;
    private long creationDate;
}
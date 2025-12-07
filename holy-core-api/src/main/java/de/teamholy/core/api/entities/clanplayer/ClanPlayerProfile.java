package de.teamholy.core.api.entities.clanplayer;

import de.teamholy.core.api.utility.ClanRank;
import eu.koboo.en2do.repository.entity.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ClanPlayerProfile {

    @Id
    UUID playerId;

    UUID clanId;
    ClanRank clanRank;

    public boolean canKick(ClanRank rank) {
        return switch (this.clanRank) {
            case LEADER -> rank != ClanRank.LEADER;
            case MOD -> rank == ClanRank.MEMBER;
            case MEMBER -> false;
        };
    }
    
}
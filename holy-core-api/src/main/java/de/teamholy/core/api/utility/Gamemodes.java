package de.teamholy.core.api.utility;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Gamemodes {

    MLGRUSH("6", "won_games", List.of("kills", "deaths", "played_games", "won_games", "destroyed_beds"), List.of("MLGRush")),
    BEDWARS("c", "won_games", List.of("kills", "deaths", "played_games", "won_games", "destroyed_beds"), List.of("BW2x1", "BW4x2", "BW8x1", "BWC2x1")),
    KNOCKBACKFFA("e", "kills", List.of("kills", "deaths"), List.of("KnockbackFFA")),
    SGFFA("a", "kills", List.of("kills", "deaths"), List.of("SGFFA")),
    CLUTCHES("§b", "", List.of(""), List.of("Clutches"));

    String color, rankingKey;
    List<String> statKeys;
    List<String> cloudGroups;
}

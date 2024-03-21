package de.teamholy.core.api.utility;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Gamemodes {

    MLGRUSH("6", "trophies", List.of(
        new StatKey("kills",0),
        new StatKey("deaths",0),
        new StatKey("played_games",0),
        new StatKey("won_games",0),
        new StatKey("destroyed_beds",0),
        new StatKey("trophies",1000)
    ), List.of("MLGRush")),
    BEDWARS("c", "trophies", List.of(
            new StatKey("kills",0),
            new StatKey("deaths",0),
            new StatKey("played_games",0),
            new StatKey("won_games",0),
            new StatKey("destroyed_beds",0),
            new StatKey("trophies",1000))
        , List.of("BW2x1", "BW4x2", "BW8x1")),
    KNOCKBACKFFA("e", "trophies", List.of(
        new StatKey("kills",0),
        new StatKey("deaths",0),
        new StatKey("trophies",1000)
    ), List.of("KnockbackFFA")),
    SGFFA("a", "trophies", List.of(
        new StatKey("kills",0),
        new StatKey("deaths",0),
        new StatKey("trophies",1000)
    ), List.of("SGFFA")),
    CLUTCHES("b", "", Lists.newArrayList(), List.of("Clutches")),
    BRIDGE("b", "", Lists.newArrayList(), List.of("Bridge")),
    RUSHBW("c","trophies", List.of(
        new StatKey("kills",0),
        new StatKey("deaths",0),
        new StatKey("played_games",0),
        new StatKey("won_games",0),
        new StatKey("destroyed_beds",0),
        new StatKey("trophies",1000)
    ), List.of("RBW2x1", "RBW4x2", "RBW8x1"));

    String color, rankingKey;
    List<StatKey> statKeys;
    List<String> cloudGroups;

    @Getter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class StatKey {

        String name;
        int defaultValue;
    }
}

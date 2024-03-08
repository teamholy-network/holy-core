package de.teamholy.core.api.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum ClanRank {

    LEADER("Leader"),
    MOD("Moderator"),
    MEMBER("Member");

    String fancy;

    public static String parsePrefix(ClanRank clanRank) {
        return switch (clanRank) {
            case LEADER -> "§4";
            case MOD -> "§c";
            default -> "§a";
        };
    }

    public String getFancy() {
        return parsePrefix(this) + fancy;
    }

}

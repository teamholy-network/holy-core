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
        switch (clanRank) {
            case LEADER:
                return "§4";
            case MOD:
                return "§c";
            default:
                return "§a";
        }
    }

    public String getFancy() {
        return parsePrefix(this) + fancy;
    }

}

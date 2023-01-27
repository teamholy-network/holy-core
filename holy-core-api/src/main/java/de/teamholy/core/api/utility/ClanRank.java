package de.teamholy.core.api.utility;

public enum ClanRank {

    LEADER("Leader"),
    MOD("Moderator"),
    MEMBER("Member");

    String fancy;

    ClanRank(String fancy) {
        this.fancy = fancy;
    }

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

package de.teamholy.core.api.entities.game;

public enum StatsType {

    DAILY,
    MONTHLY,
    ALLTIME;

    public String toBeauty() {
        if (this == DAILY) return "§aDaily";
        if (this == MONTHLY) return "§eMonthly";
        if (this == ALLTIME) return "§cAlltime";
        return "§7undefined";
    }
}

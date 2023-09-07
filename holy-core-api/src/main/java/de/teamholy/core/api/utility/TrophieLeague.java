package de.teamholy.core.api.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum TrophieLeague {


    UNRANKED(0, "§7Noob", -1, 4999, "§7N"),
    B1(1, "§cBronze I", 5000, 9999, "§cI"),
    B2(2, "§cBronze II", 10000, 14999, "§cII"),
    B3(3, "§cBronze III",15000 ,19999 , "§cIII"),
    S1(4, "§fSilver I", 20000, 24999, "§fI"),
    S2(5, "§fSilver II", 25000, 29999, "§fII"),
    S3(6, "§fSilver III", 30000, 34999, "§fIII"),
    G1(7, "§6Gold I", 35000, 49999, "§6I"),
    G2(8, "§6Gold II", 50000, 64999, "§6II"),
    G3(9, "§6Gold III", 65000, 79999, "§6III"),
    P1(10, "§bPlatinum I", 80000, 99999, "§bI"),
    P2(11, "§bPlatinum II", 100000, 119999, "§bII"),
    P3(12, "§bPlatinum III", 120000, 139999, "§bIII"),
    D1(13, "§dDiamond I", 140000, 159999, "§dI"),
    D2(14, "§dDiamond II", 160000, 179999, "§dII"),
    D3(15, "§dDiamond III", 180000, 199999, "§dIII"),
    C1(16, "§aEmerald I", 200000, 249999, "§aI"),
    C2(17, "§aEmerald II", 250000, 299999, "§aII"),
    C3(18, "§aEmerald III", 300000, 349999, "§aIII"),
    E1(19, "§3Champion I", 350000, 399999, "§3I"),
    E2(20, "§3Champion II", 400000,449999, "§31II"),
    E3(21, "§3Champion III", 450000, 499999, "§3III"),
    M(22, "§4Master", 500000, -1, "§4☣");

    private final int id;
    private final String name;
    private final int minRange, maxRange;
    private final String shortName;


    public static TrophieLeague getEloRank(int elo) {
        TrophieLeague trophieLeague = null;
        if (elo <= TrophieLeague.UNRANKED.getMaxRange())
            trophieLeague = TrophieLeague.UNRANKED;

        for (TrophieLeague trophieRanks : TrophieLeague.values()) {
            if (elo >= trophieRanks.getMinRange()) trophieLeague = trophieRanks;
        }
        return trophieLeague;
    }

    public static int calculateRange(int difference, int minRange, int maxRange) {
        int i = minRange;
        if (difference >= -10000 && difference <= 10000) {
            int rangeSize = (maxRange - minRange) + 1;
            int stepSize = 20000 / rangeSize;
            i = ((difference + 10000) / stepSize) + minRange;
            i = Math.max(Math.min(i, maxRange), minRange);
        } else if (difference > 10000) {
            i = maxRange;
        }
        return i;
    }


}

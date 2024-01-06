package de.teamholy.core.api.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum TrophieLeague {


    UNRANKED(0, "§7Noob", -1, 999, "§7N"),
    B1(1, "§cBronze I", 1000, 1149, "§cI"),
    B2(2, "§cBronze II", 1150, 1299, "§cII"),
    B3(3, "§cBronze III", 1300 ,1399 , "§cIII"),
    S1(4, "§fSilver I", 1400, 1899, "§fI"),
    S2(5, "§fSilver II", 1900, 2399, "§fII"),
    S3(6, "§fSilver III", 2400, 2999, "§fIII"),
    G1(7, "§6Gold I", 3000, 3499, "§6I"),
    G2(8, "§6Gold II", 3500, 3999, "§6II"),
    G3(9, "§6Gold III", 4000, 4499, "§6III"),
    P1(10, "§bPlatinum I", 4500, 4999, "§bI"),
    P2(11, "§bPlatinum II", 5000, 5499, "§bII"),
    P3(12, "§bPlatinum III", 5500, 6099, "§bIII"),
    D1(13, "§dDiamond I", 6100, 6699, "§dI"),
    D2(14, "§dDiamond II", 6700, 7499, "§dII"),
    D3(15, "§dDiamond III", 7500, 8499, "§dIII"),
    C1(16, "§aEmerald I", 8500, 9499, "§aI"),
    C2(17, "§aEmerald II", 9500, 10499, "§aII"),
    C3(18, "§aEmerald III", 10500, 14999, "§aIII"),
    E1(19, "§3Champion I", 15000, 24999, "§3I"),
    E2(20, "§3Champion II", 25000, 49999, "§3III"),
    E3(21, "§3Champion III", 50000, 99999, "§3III"),
    M(22, "§4Master", 100000, -1, "§4☣");

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

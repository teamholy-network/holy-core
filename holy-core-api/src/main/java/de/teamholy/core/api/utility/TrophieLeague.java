package de.teamholy.core.api.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum TrophieLeague {


    UNRANKED(0, "§7Noob", -1, 1099, ""),
    B1(1, "§cBronze I", 1100, 1249, "§cI"),
    B2(2, "§cBronze II", 1250, 1499, "§cII"),
    B3(3, "§cBronze III", 1500, 1749, "§cIII"),
    S1(4, "§fSilver I", 1750, 1999, "§fI"),
    S2(5, "§fSilver II", 2000, 2249, "§fII"),
    S3(6, "§fSilver III", 2250, 2499, "§fIII"),
    G1(7, "§6Gold I", 2500, 2749, "§6I"),
    G2(8, "§6Gold II", 2750, 2999, "§6II"),
    G3(9, "§6Gold III", 3000, 3249, "§6III"),
    P1(10, "§bPlatinum I", 3250, 3499, "§bI"),
    P2(11, "§bPlatinum II", 3500, 3749, "§bII"),
    P3(12, "§bPlatinum III", 3750, 3999, "§bIII"),
    D1(13, "§dDiamond I", 4000, 4499, "§dI"),
    D2(14, "§dDiamond II", 4500, 4999, "§dII"),
    D3(15, "§dDiamond III", 5000, 5999, "§dIII"),
    C1(16, "§1Champion I", 6000, 6499, "§1I"),
    C2(17, "§1Champion II", 6500, 6999, "§1II"),
    C3(18, "§1Champion III", 7000, 7499, "§1III"),
    M(19, "§4Master", 10000, -1, "§4☣");

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

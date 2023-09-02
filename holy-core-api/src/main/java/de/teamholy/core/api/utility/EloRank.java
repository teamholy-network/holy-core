package de.teamholy.core.api.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

@Getter @AllArgsConstructor
public enum EloRank {


    UNRANKED(0, "§7Unranked", -1, 1099, ""),
    B1(1, "§cBronze I", 1100, 1249, "§8[§cI§8]"),
    B2(2, "§cBronze II", 1250, 1499, "§8[§cII§8]"),
    B3(3, "§cBronze III", 1500, 1749, "§8[§cIII§8]"),
    S1(4, "§fSilver I", 1750, 1999, "§8[§fI§8]"),
    S2(5, "§fSilver II", 2000, 2249, "§8[§fII§8]"),
    S3(6, "§fSilver III", 2250, 2499, "§8[§fIII§8]"),
    G1(7, "§6Gold I", 2500, 2749, "§8[§6I§8]"),
    G2(8, "§6Gold II", 2750, 2999, "§8[§6II§8]"),
    G3(9, "§6Gold III", 3000, 3249, "§8[§6III§8]"),
    P1(10, "§3Platinum I", 3250, 3499, "§8[§3I§8]"),
    P2(11, "§3Platinum II", 3500, 3749, "§8[§3II§8]"),
    P3(12, "§3Platinum III", 3750, 3999, "§8[§3III§8]"),
    D1(13, "§dDiamond I", 4000, 4499, "§8[§dI§8]"),
    D2(14, "§dDiamond II", 4500, 4999, "§8[§dII§8]"),
    D3(15, "§dDiamond III", 5000, 5999, "§8[§dIII§8]"),
    C1(16, "§1Champion I", 6000, 6499, "§8[§1I§8]"),
    C2(17, "§1Champion II", 6500, 6999, "§8[§1II§8]"),
    C3(18, "§1Champion III", 7000, 7499, "§8[§1III§8]"),
    M(19, "§4Master", 10000, -1, "§8[§4☣§8]");

    private final int id;
    private final String name;
    private final int minRange, maxRange;
    private final String shortName;


    public static EloRank getEloRank(int elo) {
        EloRank eloRank = null;
        if (elo <= EloRank.UNRANKED.getMaxRange())
            eloRank = EloRank.UNRANKED;

        for (EloRank eloRanks : EloRank.values()) {
            if (elo >= eloRanks.getMinRange()) eloRank = eloRanks;
        }
        return eloRank;
    }
}

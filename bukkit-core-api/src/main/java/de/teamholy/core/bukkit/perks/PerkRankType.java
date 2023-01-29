package de.teamholy.core.bukkit.perks;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerkRankType {

    PLAYER("teamholy.perk.player","§7Player"),
    PREMIUM("teamholy.perk.premium","§6Premium"),
    VIP("teamholy.perk.vip","§dVIP"),
    HOLY("teamholy.perk.holy","§fHoly");

    private String permission;
    private String rankName;


}

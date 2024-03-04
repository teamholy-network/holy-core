package de.teamholy.core.bukkit.perks.model;

import de.teamholy.core.api.utility.Gamemodes;
import de.teamholy.core.bukkit.perks.enums.PerkRankType;
import de.teamholy.core.bukkit.perks.enums.PerkType;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;

import java.util.List;

/* copyright by Yassino */
@Getter
public class Perk {

    private int id;

    private String name;
    private Material material;
    private byte subId;
    private PerkType perkType;
    private long price;
    private PerkRankType perkRankType;
    private String specialText;
    private final List<Gamemodes> notSupportedGamemodes;

    //if perk is banner
    private DyeColor baseColor;
    private List<Pattern> patterns;

    public Perk(int id, String name, Material material, byte subId, PerkType perkType, long price, PerkRankType perkRankType, List<Gamemodes> notSupportedGamemodes) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.subId = subId;
        this.perkType = perkType;
        this.price = price;
        this.perkRankType = perkRankType;
        this.notSupportedGamemodes = notSupportedGamemodes;
    }

    public Perk(int id, String name, Material material, byte subId, PerkType perkType, long price, PerkRankType perkRankType, List<Gamemodes> notSupportedGamemodes, DyeColor baseColor, List<Pattern> patterns) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.subId = subId;
        this.perkType = perkType;
        this.price = price;
        this.perkRankType = perkRankType;
        this.notSupportedGamemodes = notSupportedGamemodes;
        this.baseColor = baseColor;
        this.patterns = patterns;
    }

    public void setBannerMeta(List<Pattern> patterns, DyeColor baseColor) {
        this.patterns = patterns;
        this.baseColor = baseColor;
    }

    public Perk setSpecialText(String text) {
        specialText = text;
        return this;
    }

    public boolean isBuyAble() {
        return price != -1;
    }

    public boolean isSpecial() {
        return specialText != null;
    }

    public boolean isBanner() {
        return material == Material.BANNER;
    }

    public boolean isRankPerk() {
        return price == -1 || specialText == null;
    }

}

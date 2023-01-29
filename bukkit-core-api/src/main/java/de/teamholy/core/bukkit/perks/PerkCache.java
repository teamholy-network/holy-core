package de.teamholy.core.bukkit.perks;

import de.teamholy.core.api.entities.perkplayer.PerkPlayerProfile;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class PerkCache {

    private final HashMap<UUID, PerkPlayerProfile> perkPlayerProfileHashMap = new HashMap<>();
    private final HashMap<Integer, Perk> perkHashMap = new HashMap<>();

}

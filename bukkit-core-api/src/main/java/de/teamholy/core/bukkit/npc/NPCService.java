package de.teamholy.core.bukkit.npc;

import de.teamholy.core.bukkit.npc.models.SkinEntry;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class NPCService {

    private final HashMap<UUID, SkinEntry> skinEntryHashMap = new HashMap<>();


    public NPCService() {




    }

}

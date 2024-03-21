package de.teamholy.core.bukkit.npc.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;

/* copyright by Yassino */
@Getter
@Setter
public class NPCPlayer {

    private HashMap<String, NPCEntry> npcs;
    private Player player;

    public NPCPlayer(Player player) {
        this.npcs = new HashMap<>();
        this.player = player;
    }
}

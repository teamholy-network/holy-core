package de.teamholy.core.bukkit.npc;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.npc.listeners.PlayerMoveListener;
import de.teamholy.core.bukkit.npc.listeners.ProtocolLibListener;
import de.teamholy.core.bukkit.npc.models.NPCSkinRepository;
import de.teamholy.core.bukkit.npc.models.SkinEntry;
import de.teamholy.core.bukkit.npc.tasks.UpdateLookTask;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/* copyright by Yassino */
@Getter
public class NPCService {

    private final BukkitCore bukkitCore;
    private final HashMap<UUID, SkinEntry> skinEntryHashMap = new HashMap<>();
    private final NPCSkinRepository npcSkinRepository;


    public NPCService(BukkitCore bukkitCore) {
        this.bukkitCore = bukkitCore;
        bukkitCore.getProtocolManager().addPacketListener(new ProtocolLibListener(bukkitCore));
        npcSkinRepository = bukkitCore.getCoreAPI().getMongoManager().create(NPCSkinRepository.class);

        new PlayerMoveListener(bukkitCore);
        new UpdateLookTask();
    }



}

package de.teamholy.core.bukkit.npc.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.npc.event.PlayerInteractAtNPCEvent;
import de.teamholy.core.bukkit.npc.event.action.InteractAction;
import de.teamholy.core.bukkit.npc.models.NPCEntry;
import de.teamholy.core.bukkit.npc.models.NPCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

public class ProtocolLibListener implements PacketListener {

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .types(PacketType.Play.Client.USE_ENTITY)
                .build();
    }

    @Override
    public Plugin getPlugin() {
        return BukkitCore.getInstance();
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }


    @Override
    public void onPacketReceiving(PacketEvent event) {

        NPCPlayer npcPlayer = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(event.getPlayer().getUniqueId()).getNpcPlayer();

        NPCEntry npcEntry = null;
        for (NPCEntry npc : npcPlayer.getNpcs().values()) {
            if (event.getPacket().getIntegers().read(0).equals(npc.getEntityId())) {
                npcEntry = npc;
            }
        }

        if (npcEntry != null) {
            InteractAction interactAction;
            try {
                Field field = event.getPacket().getEntityUseActions().getField(0);
                field.setAccessible(true);
                Object object = field.get(event.getPacket().getEntityUseActions().getTarget());

                String actionSyntax = (object == null) ? "" : object.toString();
                if (actionSyntax.equals("INTERACT")) {
                    interactAction = InteractAction.RIGHT_CLICK;
                } else if (actionSyntax.equals("ATTACK")) {
                    interactAction = InteractAction.LEFT_CLICK;
                } else {
                    return;
                }
                NPCEntry finalNpcEntry = npcEntry;
                Bukkit.getScheduler().runTask(BukkitCore.getInstance(), () -> Bukkit.getPluginManager().callEvent(new PlayerInteractAtNPCEvent(event.getPlayer(), finalNpcEntry, interactAction)));
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent arg0) {

    }
}

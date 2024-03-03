package de.teamholy.core.bukkit.npc.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.models.NPCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().put(player.getUniqueId(),new NPCPlayer(player));

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap()
                .remove(player.getUniqueId()).getNpcs().forEach((s, npcEntry) -> {
                    if (npcEntry.getHologram() != null)
                    npcEntry.getHologram().delete();
                });
    }

}

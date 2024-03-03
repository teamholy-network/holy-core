package de.teamholy.core.bukkit.npc.listeners;

import de.teamholy.api.BukkitHolyAPI;
import de.teamholy.api.bukkit.npc.models.NPCEntry;
import de.teamholy.api.bukkit.npc.models.NPCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class PlayerMoveListener implements Listener {

    @EventHandler
    public final void onWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        NPCPlayer playerEntry = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId());
        if (playerEntry == null)
        	return;
        playerEntry.getNpcs().values().forEach(NPCEntry::remove);
    }

    @EventHandler
    public final void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        NPCPlayer playerEntry = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(player.getUniqueId());
        if (playerEntry == null)
            return;
        playerEntry.getNpcs().values().forEach(NPCEntry::update);
    }


    @EventHandler
    public final void onPlayerMove(final PlayerMoveEvent event) {
        if ((event.getFrom().getBlockX() == event.getTo().getBlockX())
                && (event.getFrom().getBlockY() == event.getTo().getBlockY())
                && (event.getFrom().getBlockZ() == event.getTo().getBlockZ())
                && (event.getFrom().getWorld() == event.getTo().getWorld())) {
            return;
        }
        BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().values().forEach(playerEntry -> playerEntry.getNpcs().values().forEach(NPCEntry::update));
    }

    @EventHandler
    public final void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.getEntity() != null) {
            NPCPlayer playerEntry = BukkitHolyAPI.getInstance().getBukkitCacheHandler().getNpcPlayerHashMap().get(event.getEntity().getUniqueId());
            playerEntry.getNpcs().values().forEach(NPCEntry::remove);
        }
    }
}

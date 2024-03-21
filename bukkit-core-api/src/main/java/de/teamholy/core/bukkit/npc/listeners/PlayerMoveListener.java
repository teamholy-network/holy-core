package de.teamholy.core.bukkit.npc.listeners;

import de.teamholy.core.bukkit.BukkitCore;
import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import de.teamholy.core.bukkit.npc.models.NPCEntry;
import de.teamholy.core.bukkit.npc.models.NPCPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public final class PlayerMoveListener implements Listener {

    public PlayerMoveListener(BukkitCore bukkitCore) {
        bukkitCore.getServer().getPluginManager().registerEvents(this, bukkitCore);
    }

    @EventHandler
    public final void onWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        NPCPlayer playerEntry = getNpcPlayer(player.getUniqueId());
        if (playerEntry == null)
            return;
        playerEntry.getNpcs().values().forEach(NPCEntry::remove);
    }

    @EventHandler
    public final void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        NPCPlayer playerEntry = getNpcPlayer(player.getUniqueId());
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
        BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().values().forEach(playerEntry -> playerEntry.getNpcPlayer().getNpcs().values().forEach(NPCEntry::update));
    }

    @EventHandler
    public final void onPlayerDeath(final PlayerDeathEvent event) {
        if (event.getEntity() != null) {
            NPCPlayer playerEntry = getNpcPlayer(event.getEntity().getUniqueId());
            playerEntry.getNpcs().values().forEach(NPCEntry::remove);
        }
    }

    private NPCPlayer getNpcPlayer(UUID uuid) {
        PlayerCacheManager.CachedBukkitPlayer cachedPlayer = BukkitCore.getInstance().getPlayerCacheManager().getCachedPlayers().get(uuid);
        if (cachedPlayer == null) return null;

        return cachedPlayer.getNpcPlayer();
    }
}

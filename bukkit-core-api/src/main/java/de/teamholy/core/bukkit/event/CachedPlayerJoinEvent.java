package de.teamholy.core.bukkit.event;

import de.teamholy.core.bukkit.manager.PlayerCacheManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/* copyright by Yassino */
public class CachedPlayerJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final PlayerCacheManager.CachedBukkitPlayer cachedBukkitPlayer;

    public CachedPlayerJoinEvent(PlayerCacheManager.CachedBukkitPlayer cachedBukkitPlayer) {
        this.cachedBukkitPlayer = cachedBukkitPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerCacheManager.CachedBukkitPlayer getCachedBukkitPlayer() {
        return cachedBukkitPlayer;
    }
}

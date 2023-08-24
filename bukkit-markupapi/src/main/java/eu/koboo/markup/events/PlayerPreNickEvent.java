package eu.koboo.markup.events;

import eu.koboo.markup.util.PlayerMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPreNickEvent extends Event implements Cancellable {

    protected static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final PlayerMeta playerMeta;
    private boolean cancelled;

    public PlayerPreNickEvent(Player player, PlayerMeta playerMeta) {
        this.player = player;
        this.playerMeta = playerMeta;
        this.cancelled = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerMeta getPlayerMeta() {
        return playerMeta;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}

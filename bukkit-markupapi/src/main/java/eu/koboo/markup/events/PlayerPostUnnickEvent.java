package eu.koboo.markup.events;

import eu.koboo.markup.util.PlayerMeta;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPostUnnickEvent extends Event {

    protected static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final PlayerMeta playerMeta;

    public PlayerPostUnnickEvent(Player player, PlayerMeta playerMeta) {
        this.player = player;
        this.playerMeta = playerMeta;
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
    public HandlerList getHandlers() {
        return handlers;
    }

}

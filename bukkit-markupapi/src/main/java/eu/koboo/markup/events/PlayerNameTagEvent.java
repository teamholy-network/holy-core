package eu.koboo.markup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerNameTagEvent extends Event {

    protected static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Player other;
    private int sortId;
    private String prefix;
    private String suffix;
    private String displaySuffix;

    public PlayerNameTagEvent(Player player, Player other) {
        this.player = player;
        this.other = other;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOther() {
        return other;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDisplaySuffix() {return displaySuffix;}

    public void setDisplaySuffix(String displaySuffix) {this.displaySuffix = displaySuffix;}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}

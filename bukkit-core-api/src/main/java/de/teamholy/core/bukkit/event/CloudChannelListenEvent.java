package de.teamholy.core.bukkit.event;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/* copyright by Yassino */
@Getter
public class CloudChannelListenEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private JsonDocument data;
    private String message, channel;

    public CloudChannelListenEvent(JsonDocument data, String message, String channel) {
        this.data = data;
        this.message = message;
        this.channel = channel;
    }


    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}

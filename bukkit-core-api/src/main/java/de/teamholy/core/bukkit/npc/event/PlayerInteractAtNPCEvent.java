package de.teamholy.core.bukkit.npc.event;

import de.teamholy.core.bukkit.npc.event.action.InteractAction;
import de.teamholy.core.bukkit.npc.models.NPCEntry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerInteractAtNPCEvent extends Event implements Cancellable {
	
	public static HandlerList handlers = new HandlerList();
	public boolean cancelled = false;
	
	private final Player player;
	private final NPCEntry npcEntry;
	private final InteractAction interactAction;

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}
}

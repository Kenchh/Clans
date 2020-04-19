package me.rey.clans.events.custom;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ContainerOpenEvent extends Event implements Cancellable{
	
	private final Player player;
	private final Block container;
	private boolean isCancelled;
	private boolean allowed;
	
	public ContainerOpenEvent(Player player, Block container, boolean allowed) {
		this.container = container;
		this.player = player;
		this.isCancelled = false;
		this.allowed = allowed;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Block getContainer() {
		return container;
	}
	
	public boolean isAllowed() {
		return allowed;
	}
	
	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}

}

package me.rey.clans.sidebar;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateScoreboardEvent extends Event {
	
	private CustomScoreboard sb;
	
	public UpdateScoreboardEvent(CustomScoreboard sb) {
		this.sb = sb;
	}

	public static final HandlerList HANDLERS = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public CustomScoreboard getScoreboard() {
		return sb;
	}

}

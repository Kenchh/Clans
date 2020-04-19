package me.rey.clans.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.rey.clans.clans.Clan;

public class WarpointChangeEvent extends Event {

	private Clan killer, player;
	private long killerWarpoints;
	
	public WarpointChangeEvent(Clan killer, Clan player, long killerWarpoints) {
		this.killer = killer;
		this.player = player;
		this.killerWarpoints = killerWarpoints;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public Clan getKiller() {
		return killer;
	}
	
	public long getWarpoints() {
		return killerWarpoints;
	}
	
	public Clan getClan() {
		return player;
	}
	
}

package me.rey.clans.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WarpointChangeEvent extends Event {

	private Player killer, player;
	private long killerWarpoints;
	
	public WarpointChangeEvent(Player killer, Player player, long killerWarpoints) {
		this.killer = killer;
		this.player = player;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public Player getKiller() {
		return killer;
	}
	
	public long getWarpoints() {
		return killerWarpoints;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}

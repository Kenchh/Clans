package me.rey.clans.events.clans;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.rey.clans.clans.Clan;

public class ClanEvent extends Event {

	Clan clan;
	
	public ClanEvent(Clan clan) {
		this.clan = clan;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public Clan getClan() {
		return clan;
	}
	
	public static abstract class ClanPlayerEvent extends ClanEvent {
		
		Player issuer;
		
		public ClanPlayerEvent(Clan clan, Player issuer) {
			super(clan);
			
			this.issuer = issuer;
		}
		
		public Player getIssuer() {
			return issuer;
		}
		
	}
	
}

package me.rey.clans.events.clans;

import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.events.clans.ClanEvent.ClanPlayerEvent;

public class ClanHierarchyEvent extends ClanPlayerEvent {

	ClansRank origin, destination;
	ClansPlayer issued;
	HierarchyAction action;
	HierarchyReason reason;
	
	public ClanHierarchyEvent(Clan clan, Player issuer, HierarchyAction action, HierarchyReason reason, ClansPlayer issued, ClansRank origin, ClansRank destination) {
		super(clan, issuer);
		
		this.origin = origin;
		this.destination = destination;
		this.issued = issued;
		this.action = action;
		this.reason = reason;
	}
	
	public ClansRank getRankFrom() {
		return origin;
	}
	
	public ClansRank getRankTo() {
		return destination;
	}
	
	public HierarchyAction getAction() {
		return action;
	}
	
	public HierarchyReason getReason() {
		return reason;
	}
	
	public ClansPlayer getAffected() {
		return issued;
	}
	
	public enum HierarchyAction {
		PROMOTE, DEMOTE;
	}
	
	public enum HierarchyReason {
		STAFF, NORMAL;
	}

}

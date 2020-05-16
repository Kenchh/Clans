package me.rey.clans.events.clans;

import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.events.clans.ClanEvent.ClanPlayerEvent;

public class ClanUpdateRelationEvent extends ClanPlayerEvent {

	Clan related;
	RelationAction action;
	
	public ClanUpdateRelationEvent(Clan clan, Clan related, Player issuer, RelationAction action) {
		super(clan, issuer);
		
		this.action = action;
		this.related = related;
	}
	
	public Clan getRelated() {
		return related;
	}
	
	public RelationAction getAction() {
		return action;
	}
	
	public enum RelationAction {
		NEUTRAL, REQUEST_ALLY, ACCEPT_ALLY;
	}

}

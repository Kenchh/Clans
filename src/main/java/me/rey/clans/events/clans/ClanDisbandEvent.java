package me.rey.clans.events.clans;

import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.events.clans.ClanEvent.ClanPlayerEvent;

public class ClanDisbandEvent extends ClanPlayerEvent {

	private DisbandReason reason;
	
	public ClanDisbandEvent(Clan clan, Player issuer, DisbandReason reason) {
		super(clan, issuer);
		
		this.reason = reason;
	}
	
	public DisbandReason getReason() {
		return reason;
	}
	
	public enum DisbandReason {
		STAFF, NORMAL;
	}

}

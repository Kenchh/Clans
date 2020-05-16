package me.rey.clans.events.clans;

import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.events.clans.ClanEvent.ClanPlayerEvent;

public class ClanJoinEvent extends ClanPlayerEvent {

	JoinReason reason;
	
	public ClanJoinEvent(Clan clan, Player issuer, JoinReason reason) {
		super(clan, issuer);
		
		this.reason = reason;
	}
	
	public JoinReason getReason() {
		return reason;
	}

	public enum JoinReason {
		FORCE, INVITE;
	}
	
}

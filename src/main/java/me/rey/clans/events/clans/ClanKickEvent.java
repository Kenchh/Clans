package me.rey.clans.events.clans;

import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.events.clans.ClanEvent.ClanPlayerEvent;

public class ClanKickEvent extends ClanPlayerEvent {

	ClansPlayer kicked;
	
	public ClanKickEvent(Clan clan, Player issuer, ClansPlayer kicked) {
		super(clan, issuer);
		
		this.kicked = kicked;
	}
	
	public ClansPlayer getKicked() {
		return kicked;
	}

}

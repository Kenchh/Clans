package me.rey.clans.events.clans;

import me.rey.clans.clans.Clan;

public class ClanWarpointEvent extends ClanEvent {

	private Clan killer;
	private long killerWarpoints;
	
	public ClanWarpointEvent(Clan killer, Clan player, long killerWarpoints) {
		super(killer);
		this.killer = killer;
		this.killerWarpoints = killerWarpoints;
	}
	
	public Clan getKilled() {
		return killer;
	}
	
	public long getKillerWarpoints() {
		return killerWarpoints;
	}
	
}

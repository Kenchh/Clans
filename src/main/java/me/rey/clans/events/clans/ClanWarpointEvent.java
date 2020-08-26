package me.rey.clans.events.clans;

import me.rey.clans.clans.Clan;

public class ClanWarpointEvent extends ClanEvent {

	private Clan player;
	private long killerWarpoints;
	
	public ClanWarpointEvent(Clan killer, Clan player, long killerWarpoints) {
		super(killer);
		this.player = player;
		this.killerWarpoints = killerWarpoints;
	}
	
	public Clan getKilled() {
		return player;
	}
	
	public long getKillerWarpoints() {
		return killerWarpoints;
	}
	
}

package me.rey.clans.events.clans;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.events.clans.ClanEvent.ClanPlayerEvent;

public class ClanTerritoryUnclaimEvent extends ClanPlayerEvent {

	boolean allClaims;
	UnclaimReason reason;
	ArrayList<Chunk> chunks;
	
	public ClanTerritoryUnclaimEvent(Clan clan, Player issuer, ArrayList<Chunk> chunks, UnclaimReason reason, boolean wasAllClaims) {
		super(clan, issuer);
		this.allClaims = wasAllClaims;
		this.reason = reason;
		this.chunks = chunks;
	}
	
	public ArrayList<Chunk> getChunks(){
		return chunks;
	}
	
	public UnclaimReason getReason() {
		return reason;
	}
	
	public boolean isAllClaims() {
		return allClaims;
	}
	
	public enum UnclaimReason {
		ENERGY, FORCE, NORMAL;
	}

}

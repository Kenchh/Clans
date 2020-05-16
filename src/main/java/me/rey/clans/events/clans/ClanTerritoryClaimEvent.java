package me.rey.clans.events.clans;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.events.clans.ClanEvent.ClanPlayerEvent;

public class ClanTerritoryClaimEvent extends ClanPlayerEvent {

	boolean allClaims;
	ArrayList<Chunk> chunks;
	
	public ClanTerritoryClaimEvent(Clan clan, Player issuer, ArrayList<Chunk> chunks) {
		super(clan, issuer);
		this.chunks = chunks;
	}
	
	public ArrayList<Chunk> getChunks(){
		return chunks;
	}
	
}

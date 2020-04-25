package me.rey.clans.clans;

import org.bukkit.ChatColor;

import me.rey.clans.utils.Text;

public enum ClanRelations {
	
	NEUTRAL(0, ChatColor.GOLD, ChatColor.YELLOW, false),
	ALLY(1, ChatColor.DARK_GREEN, ChatColor.GREEN),
//	TRUCE(2, ChatColor.DARK_GRAY, ChatColor.GRAY), // Removed
	ENEMY(3, ChatColor.DARK_RED, ChatColor.RED, false),
	SELF(4, ChatColor.DARK_AQUA, ChatColor.AQUA, false);
	
	private final int id;
	private final ChatColor clanName, playerName;
	private boolean shouldSave;
	
	ClanRelations(int id, ChatColor clanName, ChatColor playerName, boolean shouldSave){
		this.id = id;
		this.clanName = clanName;
		this.playerName = playerName;
		this.shouldSave = shouldSave;
	}
	
	ClanRelations(int id, ChatColor clanName, ChatColor playerName){
		this.id = id;
		this.clanName = clanName;
		this.playerName = playerName;
		this.shouldSave = true;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return Text.formatName(this.name());
	}
	
	public ChatColor getClanColor() {
		return clanName;
	}
	
	public ChatColor getPlayerColor() {
		return playerName;
	}
	
	public static ClanRelations getRelationFromId(int id) {
		for(ClanRelations relation : ClanRelations.values()) {
			if(relation.getId() == id)
				return relation;
		}
		return null;
	}
	
	public boolean shouldSave() {
		return shouldSave;
	}

}

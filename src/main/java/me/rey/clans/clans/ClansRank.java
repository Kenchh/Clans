package me.rey.clans.clans;

import org.bukkit.ChatColor;

import me.rey.clans.utils.Text;

public enum ClansRank {
	
	NONE(81, 0),
	RECRUIT(61, 200),
	MEMBER(41, 300),
	ADMIN(21, 400, ChatColor.RED),
	LEADER(1, 500, ChatColor.DARK_RED);
	
	private int power, id;
	private ChatColor color;
	
	ClansRank(int id, int power, ChatColor color){
		this.id = id;
		this.power = power;
		this.color = color;
	}
	
	ClansRank(int id, int power){
		this.id = id;
		this.power = power;
	}
	
	public int getPower() {
		return power;
	}
	
	public String getName() {
		return Text.formatName(this.name());
	}
	
	public int getId() {
		return id;
	}
	
	public ChatColor getColor() {
		return color == null ? ChatColor.WHITE : color;
	}
	
	public static ClansRank getRankFromId(int id) {
		for(ClansRank rank : ClansRank.values()) {
			if(rank.getId() == id) return rank;
		}
		return null;
	}

}

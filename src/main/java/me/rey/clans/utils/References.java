package me.rey.clans.utils;

import org.bukkit.Material;

import me.ewmikey.ranks.UserRank;
import me.rey.clans.Main;

public class References {
	
	public static final int MAX_ALLIES = 2;
	public static final int MAX_TRUCES = 1;
	public static final int MAX_MEMBERS = 13;
	public static final int MAX_TERRITORY = 8;
	public static final long MAX_ENERGY = 10000;
	public static final double MAX_ENERGY_DAYS = 7.0;
	public static final Material HOME_BLOCK = Material.BEACON;
	public static final int SIEGE_MINUTES = 1;

	private static final String staffRankName = Main.getInstance().getConfig().getString("staff-permission-rank");
	
	public static UserRank getStaffRank() {
		for(UserRank ur : UserRank.values()) {
			if(ur.getName().equalsIgnoreCase(staffRankName))
				return ur;
		}
		return UserRank.ADMIN;
	}

}

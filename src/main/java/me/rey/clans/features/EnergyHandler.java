package me.rey.clans.features;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.database.SQLManager;

public class EnergyHandler extends BukkitRunnable {

	/*
	 *  CURRENT MAX ENERGY: 10,000
	 *  ENERGY DECREMENT PER MINUTE: -1
	 *  ENERGY DECREMENT PER HOUR: -60
	 *  ENERGY DECREMENT PER DAY: 1440
	 *  ENERGY DECREMENT PER WEEK: ~10,000
	 * 
	 */
	
	private final int intervalMinutes = 1;
	private final int decrement = 1;
	private SQLManager sql;
	
	public EnergyHandler(SQLManager sql) {
		this.sql = sql;
	}
	
	public void start() {
		this.runTaskTimer(JavaPlugin.getPlugin(Main.class), 0, (intervalMinutes * 60) * 20);
	}
	
	@Override
	public void run() {
		ArrayList<UUID> clans = Main.clans;
		
		for(UUID query : clans) {
			Clan toDecrease = this.sql.getClan(query);
			if(toDecrease.isServerClan()) continue;
			if(toDecrease.getEnergy() <= 0) continue;
			if(toDecrease.getTerritory().size() == 0) continue;
			
			toDecrease.setEnergy(toDecrease.getEnergy() - this.decrement);
			this.sql.saveClan(toDecrease);
			
			if(toDecrease.getEnergy() <= 0) {
				toDecrease.unclaimAll();
				toDecrease.announceToClan("Your Clan Energy has &qDEPLETED&r!");
				this.sql.saveClan(toDecrease);
			}
			
		}
	}

}

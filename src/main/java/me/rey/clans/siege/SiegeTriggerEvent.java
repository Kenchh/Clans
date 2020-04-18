package me.rey.clans.siege;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.events.custom.WarpointChangeEvent;

public class SiegeTriggerEvent implements Listener {
	
	@EventHandler
	public void onSiege(WarpointChangeEvent e) {
		if(e.getWarpoints() < 25) return;
		
		Clan sieger = new ClansPlayer(e.getKiller()).getClan();
		Clan sieged = new ClansPlayer(e.getPlayer()).getClan();
	}

}

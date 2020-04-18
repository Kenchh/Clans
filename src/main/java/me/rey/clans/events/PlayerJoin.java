package me.rey.clans.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.rey.clans.packets.PlayerInfo;
import me.rey.clans.utils.Text;

public class PlayerJoin implements Listener {
	
	PlayerInfo info = new PlayerInfo();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		e.setJoinMessage(Text.color("&8Join> &7" + e.getPlayer().getName()));
		info.setupSidebar(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.setQuitMessage(Text.color("&8Quit> &7" + e.getPlayer().getName()));
	}
	
}

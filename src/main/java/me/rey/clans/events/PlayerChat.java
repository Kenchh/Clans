package me.rey.clans.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.utils.Text;

public class PlayerChat implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(e.isCancelled()) return;
		e.setCancelled(true);
		Player p = e.getPlayer();

		
		for(Player online : Bukkit.getOnlinePlayers()) {
			ClansPlayer toSend = new ClansPlayer(online);
			toSend.getPlayer().sendMessage(Text.color(Text.formatClanColors(p, online) + " &f") + e.getMessage());
		}
		
		Text.echo("Chat", p.getName() + ": " + e.getMessage());
	}

}

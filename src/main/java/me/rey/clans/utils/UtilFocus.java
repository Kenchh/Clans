package me.rey.clans.utils;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.rey.clans.clans.ClansPlayer;

public class UtilFocus implements Listener {
	
	public static final ChatColor PLAYER_FOCUS = ChatColor.LIGHT_PURPLE;
	public static final ChatColor CLAN_FOCUS = ChatColor.DARK_PURPLE;

	private static HashMap<Player, Player> focusing = new HashMap<>();
	
	public static void focus(Player root, Player toFocus) {
		if(toFocus == null || !toFocus.isOnline())
			focusing.remove(root);
		else if(focusing.containsKey(root))
			focusing.replace(root, toFocus);
		else
			focusing.put(root, toFocus);
	}
	
	public static Player getFocus(Player from) {
		return focusing.containsKey(from) ? focusing.get(from) : null;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(focusing.containsKey(e.getPlayer()))
			focusing.remove(e.getPlayer());
		if(focusing.containsValue(e.getPlayer()))
			focusing.forEach((key,value) -> {
				if(value.equals(e.getPlayer())) {
					focusing.remove(key);
					new ClansPlayer(key).sendMessageWithPrefix("Focus", "&q" + value.getName() + " &rhas logged off!");
				}
		});
	}
	
}

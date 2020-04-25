package me.rey.clans.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.commands.AllyChat;
import me.rey.clans.commands.ClanChat;
import me.rey.clans.utils.Text;

public class PlayerChat implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(e.isCancelled()) return;
		e.setCancelled(true);
		
		if(ClanChat.inChat.contains(e.getPlayer().getUniqueId()) || AllyChat.inChat.contains(e.getPlayer().getUniqueId())) {
			Clan self = new ClansPlayer(e.getPlayer()).getClan();
			if(self == null) {
				ClanChat.inChat.remove(e.getPlayer().getUniqueId());
				AllyChat.inChat.remove(e.getPlayer().getUniqueId());
				return;
			}
			
			if(AllyChat.inChat.contains(e.getPlayer().getUniqueId())) {
				self.shoutToRelation(ClanRelations.ALLY, e.getPlayer(), e.getMessage());
				return;
			}
			
			if(ClanChat.inChat.contains(e.getPlayer().getUniqueId())) {
				self.shoutToRelation(ClanRelations.SELF, e.getPlayer(), e.getMessage());
				return;
			}
			
			return;
		}
		
		Player p = e.getPlayer();

		
		for(Player online : Bukkit.getOnlinePlayers()) {
			ClansPlayer toSend = new ClansPlayer(online);
			toSend.getPlayer().sendMessage(Text.color(Text.getPrefix(e.getPlayer()) + Text.formatClanColors(p, online) + " &f") + e.getMessage());
		}
		
		Text.echo("Chat", p.getName() + ": " + e.getMessage());
	}

}

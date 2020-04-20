package me.rey.clans.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.database.SQLManager;
import me.rey.clans.packets.Title;
import me.rey.clans.utils.Text;

public class TerritoryChange implements Listener {
	
	SQLManager sql = Main.getInstance().getSQLManager();
	
	private final int titleDelaySeconds = 2;
	
	@EventHandler
	public void onLogin(PlayerJoinEvent e) {
		this.sendTitle(e.getPlayer(), e.getPlayer().getLocation(), null);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Location to = e.getTo(), from = e.getFrom();
		
		if(to.getChunk() == from.getChunk()) return;
		
		this.sendTitle(e.getPlayer(), to, from);
	}
	
	private void sendTitle(Player p, Location to, Location from) {
		ClansPlayer cp = new ClansPlayer(p);
		
		Clan owner = Main.getInstance().getClanFromTerritory(to.getChunk());
		if(from == null) {
			if(owner == null) {
				cp.sendMessageWithPrefix("Territory", "Wilderness");
				new Title("", Text.color("&7Wilderness"), 5, titleDelaySeconds * 20, 5).send(p);
				return;
			}
			
			ClanRelations relation = ClanRelations.NEUTRAL;
			if(cp.hasClan())
				relation = cp.getClan().getClanRelation(owner.getUniqueId());
			
			cp.sendMessageWithPrefix("Territory", relation.getPlayerColor() + owner.getName());
			new Title("", Text.color(relation.getPlayerColor() + owner.getName()), 5, titleDelaySeconds * 20, 5).send(p);
			return;
		}
		
		Clan clanFrom = Main.getInstance().getClanFromTerritory(from.getChunk());
		if(owner == null) {
			if(!(clanFrom == null && owner == null)) {
				cp.sendMessageWithPrefix("Territory", "Wilderness");
				new Title("", Text.color("&7Wilderness"), 5, titleDelaySeconds * 20, 5).send(p);
			}
			return;
		}
		
		ClanRelations relation = ClanRelations.NEUTRAL;
		if(cp.hasClan())
			relation = cp.getClan().getClanRelation(owner.getUniqueId());
		
		if(owner.compare(clanFrom)) return;
		cp.sendMessageWithPrefix("Territory", relation.getPlayerColor() + owner.getName());
		new Title("", Text.color(relation.getPlayerColor() + owner.getName()), 5, titleDelaySeconds * 20, 5).send(p);
	}

}

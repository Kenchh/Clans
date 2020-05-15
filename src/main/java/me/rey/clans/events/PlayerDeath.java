package me.rey.clans.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.database.SQLManager;
import me.rey.clans.events.custom.WarpointChangeEvent;
import me.rey.clans.siege.Siege;
import me.rey.core.events.customevents.combat.DeathEvent;
import me.rey.core.players.combat.DeathMessage;
import me.rey.core.players.combat.PlayerHit;

public class PlayerDeath implements Listener {
	
	private SQLManager sql = Main.getInstance().getSQLManager();
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onDeath(DeathEvent e) {
		e.cancelDeathMessage(true);
		
		Player player = e.getPlayer();
		ClansPlayer cp = new ClansPlayer(player);
		PlayerHit lastBlow = e.getLastHit();
		DeathMessage dm = e.getDeathMessage();
		
		for(Player online : Bukkit.getOnlinePlayers()) {
			ClansPlayer to = new ClansPlayer(online);
			ClanRelations relation = cp.hasClan() ? to.hasClan() ? cp.getClan().getClanRelation(to.getClan().getUniqueId()) : ClanRelations.NEUTRAL : ClanRelations.NEUTRAL;
	
			dm.setPlayerName(relation.getPlayerColor() + ChatColor.stripColor(dm.getPlayerName()));
			
			if(lastBlow != null && lastBlow.isCausedByPlayer()) {				
				Player killer = (Player) lastBlow.getEntityCause();
				ClansPlayer cpk = new ClansPlayer(killer);
				ClanRelations kRelation = cpk.hasClan() ? to.hasClan() ? cpk.getClan().getClanRelation(to.getClan().getUniqueId()) : ClanRelations.NEUTRAL : ClanRelations.NEUTRAL;
	
				dm.setKillerName(kRelation.getPlayerColor() + ChatColor.stripColor(dm.getKillerName()));
			}
			
			to.sendMessage(dm.get());
		}
		
		if(lastBlow != null) {
			Player k = (Player) lastBlow.getEntityCause();
			ClansPlayer killer = new ClansPlayer(k);
			
			if(!killer.hasClan() || !cp.hasClan()) return;
			
			Clan toGiveWP = killer.getClan();
			Clan toLoseWP = cp.getClan();
		
			boolean isInSiege = false;
			for(Siege siege : toGiveWP.getClansSiegedBySelf()) {
				if(siege.getClanSieged().getUniqueId().equals(toLoseWP.getUniqueId()))
					isInSiege = true;
			}
			
			for(Siege siege : toGiveWP.getClansSiegingSelf()) {
				if(siege.getClanSieging().getUniqueId().equals(toLoseWP.getUniqueId()))
					isInSiege = true;
			}
			
			
			// Cancelling Warpoint in siege
			if(isInSiege) return;
			
			long lost = toLoseWP.setWarpoint(toGiveWP.getUniqueId(), toLoseWP.getWarpointsOnClan(toGiveWP.getUniqueId())-1); // Removing WP on clan that died
			long won = toGiveWP.setWarpoint(toLoseWP.getUniqueId(), toGiveWP.getWarpointsOnClan(toLoseWP.getUniqueId())+1); // Adding WP on clan that got kill
			ChatColor color = lost <= -10 || lost >= 10 ? ChatColor.DARK_RED : ChatColor.YELLOW;
			String sLost = lost > 0 ? "+" : "";
			String sWon = won > 0 ? "+" : "";
			
			toLoseWP.announceToClan("&9(!) &7Your clan has &qLOST &ra War Point to &s" + color + toGiveWP.getName() + " &7(" + color + sLost + lost + "&7).", false);
			toGiveWP.announceToClan("&9(!) &7Your clan has &wGAINED &ra War Point on &s" + color + toLoseWP.getName() + " &7(" + color + sWon + won + "&7).", false);
			
			WarpointChangeEvent event = new WarpointChangeEvent(toGiveWP, toLoseWP, won);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			sql.saveClan(toGiveWP);
			sql.saveClan(toLoseWP);
		}
	}

}

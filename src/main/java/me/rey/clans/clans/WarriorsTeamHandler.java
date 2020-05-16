package me.rey.clans.clans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.rey.clans.Main;
import me.rey.core.events.customevents.team.TeamProcessEvent;

public class WarriorsTeamHandler implements Listener {
	
	public final List<ClanRelations> TEAMMATES = Arrays.asList(ClanRelations.ALLY, ClanRelations.SELF);
	
	@EventHandler 
	public void onTeamProcess(TeamProcessEvent e) {
		updateTeams(e, e.getPlayer());
	}
	
	public void updateTeams(TeamProcessEvent e, Player p) {
		Set<Player> teammates = new HashSet<Player>();
		
		ClansPlayer cp = new ClansPlayer(p);
		if(cp.getRealClan() != null) {
			
			for(ClansPlayer cm : cp.getRealClan().getOnlinePlayers(false).keySet())
				e.addTeammate(cm.getPlayer());
			
			for(UUID uuid : cp.getRealClan().getRelations().keySet()) 
				if(this.TEAMMATES.contains(cp.getRealClan().getClanRelation(uuid)))
					for(ClansPlayer cm : Main.getInstance().getClan(uuid).getOnlinePlayers(false).keySet())
						e.addTeammate(cm.getPlayer());
		}
		
		for(Player z : teammates)
			e.addTeammate(z);
	}

}

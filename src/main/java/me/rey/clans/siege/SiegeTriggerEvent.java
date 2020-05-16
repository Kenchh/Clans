package me.rey.clans.siege;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.events.clans.ClanWarpointEvent;
import me.rey.clans.events.custom.ContainerOpenEvent;

public class SiegeTriggerEvent implements Listener {
	
	@EventHandler
	public void onSiege(ClanWarpointEvent e) {
		if(e.getKillerWarpoints() < 25) return;
		
		Clan sieger = e.getClan();
		Clan sieged = e.getKilled();
		
		Siege siege = new Siege(sieger, sieged, System.currentTimeMillis());
		siege.start();
		
		sieger.setWarpoint(sieged.getUniqueId(), 0);
		sieged.setWarpoint(sieger.getUniqueId(), 0);
		Main.getInstance().getSQLManager().saveClan(sieger); // saving SIEGER
		Main.getInstance().getSQLManager().saveClan(sieged); // saving SIEGED
	}
	
	@EventHandler
	public void onOpenContainer(ContainerOpenEvent e) {
		if(!isInSiegerTerritory(e.getPlayer(), e.getContainer())) return;
		
		e.setAllowed(true);
	}
	

	private Clan isInOtherClaim(Player player, Block block) {
		Clan owner = Main.getInstance().getClanFromTerritory(block.getChunk());
		ClansPlayer self = new ClansPlayer(player);
		return owner == null ? null : (self.hasClan() && self.getClan().compare(owner) ? null : owner);
	}
	
	public boolean isInSiegerTerritory(Player player, Block block) {
		if(!(new ClansPlayer(player).hasClan())) return false;
		if(isInOtherClaim(player, block) == null) return false;
		
		Clan on = isInOtherClaim(player, block);
		Clan self = new ClansPlayer(player).getClan();
		if(!Siege.sieges.containsKey(self.getUniqueId())) return false;
		if(Siege.sieges.get(self.getUniqueId()) == null || Siege.sieges.get(self.getUniqueId()).isEmpty()) return false;
		
		ArrayList<Siege> currentlySieging = Siege.sieges.get(self.getUniqueId());
		for(Siege siege : currentlySieging) {
			if(siege.getClanSieged().getUniqueId().equals(on.getUniqueId()))
				return true;
		}
		return false;
		
	}

}

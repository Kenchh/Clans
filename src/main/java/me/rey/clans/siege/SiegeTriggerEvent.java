package me.rey.clans.siege;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.events.custom.ContainerOpenEvent;
import me.rey.clans.events.custom.WarpointChangeEvent;

public class SiegeTriggerEvent implements Listener {
	
	@EventHandler
	public void onSiege(WarpointChangeEvent e) {
		if(e.getWarpoints() < 25) return;
		
		Clan sieger = e.getKiller();
		Clan sieged = e.getClan();
		Siege siege = new Siege(sieger, sieged);
		siege.start();
	}
	
	@EventHandler
	public void onOpenContainer(ContainerOpenEvent e) {
		if(!isInSiegerTerritory(e.getPlayer(), e.getContainer())) return;
		
		e.setAllowed(true);
	}
	

	private Clan isInOtherClaim(Player player, Block block) {
		Chunk chunk = block.getChunk();

		if (!Main.territory.containsKey(chunk))
			return null;

		Clan owner = Main.getInstance().getSQLManager().getClan(Main.territory.get(chunk));
		ClansPlayer cp = new ClansPlayer(player);
		if (cp.hasClan() && cp.getClan().compare(owner))
			return null;

		return owner;
	}
	
	public boolean isInSiegerTerritory(Player player, Block block) {
		if(!(new ClansPlayer(player).hasClan())) return false;
		if(isInOtherClaim(player, block) == null) return false;
		
		Clan on = isInOtherClaim(player, block);
		Clan self = new ClansPlayer(player).getClan();
		if(!Siege.sieges.containsKey(self.getUniqueId())) return false;
		if(Siege.sieges.get(self.getUniqueId()) == null || Siege.sieges.get(self.getUniqueId()).isEmpty()) return false;
		
		Set<UUID> currentlySieging = Siege.sieges.get(self.getUniqueId());
		return currentlySieging.contains(on.getUniqueId());
		
	}

}

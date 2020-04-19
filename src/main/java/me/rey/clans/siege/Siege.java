package me.rey.clans.siege;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.packets.Title;
import me.rey.clans.utils.References;
import me.rey.clans.utils.Text;

public class Siege {
	
	// Sieger -> Set<Clans they siege>
	public static HashMap<UUID, Set<UUID>> sieges = new HashMap<>();
	
	private Clan sieger, sieged;
	
	public Siege(Clan sieger, Clan sieged) {
		this.sieger = sieger;
		this.sieged = sieged;
	}
	
	public void start() {
		
		Set<UUID> currentlySieging = sieges.get(sieger.getUniqueId()) == null ? new HashSet<UUID>() : sieges.get(sieger.getUniqueId());
		
		if(currentlySieging.contains(sieged.getUniqueId())) return;
		currentlySieging.add(sieged.getUniqueId());
		
		Siege.sieges.put(sieger.getUniqueId(), currentlySieging);
		
		SiegeRunnable runnable = new SiegeRunnable(sieger, sieged);
		runnable.start();
		
		for(UUID uuid : sieger.getPlayers().keySet()) {
			ClansPlayer cp = new ClansPlayer(uuid);
			if(!cp.isOnline()) continue;
			new Title(Text.color("&c&lSIEGE STARTED!"), Text.color("&rNow sieging: &e" + sieged.getName()), 2, 5 * 20, 20).send(cp.getPlayer());
		}
		
		for(UUID uuid : sieged.getPlayers().keySet()) {
			ClansPlayer cp = new ClansPlayer(uuid);
			if(!cp.isOnline()) continue;
			new Title(Text.color("&c&lSIEGE STARTED!"), Text.color("&rBy: &s" + sieger.getName()), 2, 5 * 20, 20).send(cp.getPlayer());
		}
		
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Set<UUID> toRemove = sieges.get(sieger.getUniqueId()) == null ? new HashSet<UUID>() : sieges.get(sieger.getUniqueId());
				toRemove.remove(sieged.getUniqueId());
				runnable.cancel();
				
				Clan newSieged = Main.getInstance().getSQLManager().getClan(sieged.getUniqueId());
				Clan newSieger = Main.getInstance().getSQLManager().getClan(sieger.getUniqueId());
				
				for(UUID uuid : newSieged.getPlayers().keySet()) {
					ClansPlayer cp = new ClansPlayer(uuid);
					if(!cp.isOnline()) continue;
					new Title(Text.color("&qSIEGE ENDED!"), "", 2, 2 * 20, 5).send(cp.getPlayer());
				}
				
				for(UUID uuid : newSieger.getPlayers().keySet()) {
					ClansPlayer cp = new ClansPlayer(uuid);
					if(!cp.isOnline()) continue;
					new Title(Text.color("&qSIEGE ENDED!"), "", 2, 2 * 20, 5).send(cp.getPlayer());
				}
				
			}
			
		}.runTaskLater(Main.getInstance(), (References.SIEGE_MINUTES * 60) * 20);
		
	}
	
	public Clan getClanSieging() {
		return sieger;
	}
	
	public Clan getClanSieged() {
		return sieged;
	}

}

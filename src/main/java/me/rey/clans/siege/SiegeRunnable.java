package me.rey.clans.siege;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.packets.ActionBar;
import me.rey.core.utils.Text;

public class SiegeRunnable extends BukkitRunnable {

	private double intervalInSeconds = 0.1;
	private Clan sieging, sieged;
	
	public SiegeRunnable(Clan sieging, Clan sieged) {
		this.sieging = sieging;
		this.sieged = sieged;
	}
	
	public void start() {
		this.runTaskTimer(Main.getInstance(), 0, (int) (intervalInSeconds * 20));
	}
	
	@Override
	public void run() {

		/*
		 * NOT displaying action bar if the clan is SIEGING more than 1 clan
		 */
		Set<UUID> currentlySieging = Siege.sieges.get(sieging.getUniqueId()) == null ? new HashSet<UUID>() : Siege.sieges.get(sieging.getUniqueId());
		if(!currentlySieging.contains(sieged.getUniqueId())) return;
		Iterator<UUID> cSieging = currentlySieging.iterator();
		while(cSieging.hasNext()) {
			UUID next = cSieging.next();
			if(next.equals(sieged.getUniqueId())) {
				Clan newSieger = Main.getInstance().getSQLManager().getClan(sieging.getUniqueId());
				for(UUID uuid : newSieger.getPlayers().keySet()) {
					ClansPlayer cp = new ClansPlayer(uuid);
					if(!cp.isOnline()) continue;
					new ActionBar(Text.color("&cYou are sieging &a" + sieged.getName())).send(cp.getPlayer());
				}
			}
			return;
		}
		
		/*
		 * NOT displaying action bar if the clan is being sieged by more than 1 other clan(s)
		 */
		
		ArrayList<UUID> siegersOnSelf = new ArrayList<UUID>();
		for(UUID siegers : Siege.sieges.keySet()) {
			Set<UUID> siegerSieging = Siege.sieges.get(siegers) == null ? new HashSet<UUID>() : Siege.sieges.get(siegers);
			Iterator<UUID> siegerIterator = siegerSieging.iterator();
			while(siegerIterator.hasNext()) {
				UUID uuid = siegerIterator.next();
				if(uuid.equals(sieged.getUniqueId()))
					siegersOnSelf.add(uuid);
			}
		}
		
		Clan newSieged = Main.getInstance().getSQLManager().getClan(sieged.getUniqueId());
		for(UUID uuid : newSieged.getPlayers().keySet()) {
			if(!sieging.getUniqueId().equals(siegersOnSelf.get(0))) break;
			
			ClansPlayer cp = new ClansPlayer(uuid);
			if(!cp.isOnline()) continue;
			System.out.println("being sieged");
			new ActionBar(Text.color("&cYou are being sieged")).send(cp.getPlayer());
		}
		// END
		
	}

}

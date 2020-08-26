package me.rey.clans.siege;

import java.util.ArrayList;
import java.util.HashMap;
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
	public static HashMap<UUID, ArrayList<Siege>> sieges = new HashMap<>();
	
	private Clan sieger, sieged;
	private SiegeRunnable runnable;
	private long time;
	
	public Siege(Clan sieger, Clan sieged, long timeIssued) {
		this.sieger = sieger;
		this.sieged = sieged;
		this.time = timeIssued;

		runnable = new SiegeRunnable(sieger, sieged, this);
	}
	
	public String getRemainingString(long timeCurrent) {
		boolean seconds = false; 
		double remainingMinutes = (double) References.SIEGE_MINUTES - ((double) ((timeCurrent-time)/1000.0/60.0));
		if(remainingMinutes <= 1) {
			remainingMinutes = remainingMinutes * 60.0;
			seconds = true;
		}
		
		return String.format("%.1f", remainingMinutes <= 0 ? 0.0 : remainingMinutes ) + " " + (seconds ? "Seconds" : "Minutes");
	}
	
	public void end() {
		if(this.runnable != null)
			runnable.cancel();
		
		ArrayList<Siege> toRemove = sieges.get(sieger.getUniqueId()) == null ? new ArrayList<>() : sieges.get(sieger.getUniqueId());
		toRemove.remove(this);
		sieges.put(sieger.getUniqueId(), toRemove);
		runnable.cancel();
		
		Clan newSieged = Main.getInstance().getClan(sieged.getUniqueId());
		Clan newSieger = Main.getInstance().getClan(sieger.getUniqueId());
		
		try {
			for(UUID uuid : newSieged.getPlayers().keySet()) {
				ClansPlayer cp = new ClansPlayer(uuid);
				if(!cp.isOnline()) continue;
				new Title(Text.color("&qSIEGE ENDED!"), Text.color("&rBy: &e" + sieger.getName()), 2, 2 * 20, 5).send(cp.getPlayer());
			}
			
			for(UUID uuid : newSieger.getPlayers().keySet()) {
				ClansPlayer cp = new ClansPlayer(uuid);
				if(!cp.isOnline()) continue;
				new Title(Text.color("&qSIEGE ENDED!"), Text.color("&rOn: &e" + sieged.getName()), 2, 2 * 20, 5).send(cp.getPlayer());
			}
		} catch (Exception ignore) {
			
		}
	}
	
	public void start() {
		
		ArrayList<Siege> currentlySieging = sieges.get(sieger.getUniqueId()) == null ? new ArrayList<>() : sieges.get(sieger.getUniqueId());
		
		if(currentlySieging.contains(this)) return;
		currentlySieging.add(this);
		
		Siege.sieges.put(sieger.getUniqueId(), currentlySieging);
		
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
				end();
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

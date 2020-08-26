package me.rey.clans.siege;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;

@SuppressWarnings("unused")
public class SiegeRunnable extends BukkitRunnable {

	private double intervalInSeconds = 0.1;
	private Clan sieging, sieged;
	private Siege siege;
	
	public SiegeRunnable(Clan sieging, Clan sieged, Siege siege) {
		this.sieging = sieging;
		this.sieged = sieged;
		this.siege = siege;
	}
	
	public void start() {
		this.runTaskTimer(Main.getInstance(), 0, (int) (intervalInSeconds * 20));
	}
	
	@Override
	public void run() {

		String timeRemaining = this.siege.getRemainingString(System.currentTimeMillis());
		
		/*
		 * NOT displaying action bar if the clan is being sieged by more than 1 other clan(s)
		 */
		
		Clan newSieged = Main.getInstance().getClan(sieged.getUniqueId());
		Iterator<Siege> iterator = newSieged.getClansSiegingSelf().iterator();
		if(iterator.hasNext() || !sieging.getUniqueId().equals(iterator.next().getClanSieging().getUniqueId())) {
			for(UUID uuid : newSieged.getPlayers().keySet()) {
				
				ClansPlayer cp = new ClansPlayer(uuid);
				if(!cp.isOnline()) continue;
				
//				NOT NEEDED
//				new ActionBar(Text.color("&cSieged by &a" + sieging.getName() + String.format(" &c(&a%s&c)", timeRemaining))).send(cp.getPlayer());
			}
		}
		// END
		
		/*
		 * NOT displaying action bar if the clan is SIEGING more than 1 clan
		 */
		Clan newSieger = Main.getInstance().getClan(sieging.getUniqueId());
		if(newSieger.isBeingSieged()) return;
		Iterator<Siege> iterator2 = newSieger.getClansSiegedBySelf().iterator();
		if(iterator2.hasNext() && sieged.getUniqueId().equals(iterator2.next().getClanSieged().getUniqueId())) {
			for(UUID uuid : newSieger.getPlayers().keySet()) {
				
				ClansPlayer cp = new ClansPlayer(uuid);
				if(!cp.isOnline()) continue;
				
//				NOT NEEDED
//				new ActionBar(Text.color("&cSieging &a" + sieged.getName() + String.format(" &c(&a%s&c)", timeRemaining))).send(cp.getPlayer());
			}
		}
		
	}

}

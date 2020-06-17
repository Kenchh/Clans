package me.rey.clans.worldevents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.reinforced.WorldEvents;
import com.reinforced.bosses.Boss;
import com.reinforced.bosses.BossAbilitiesHandler;
import com.reinforced.bosses.events.BossDisplay.BossSpawnEvent;
import com.reinforced.bosses.events.BossUpdateHealthEvent;

import me.rey.clans.Main;
import me.rey.clans.worldevents.queenabilities.Blackjack;
import me.rey.core.effects.ParticleEffect.ColoredParticle;
import me.rey.core.utils.UtilBlock;
import net.md_5.bungee.api.ChatColor;

public class ClansEvents implements Listener {

	public static Boss QUEEN_OF_HEARTS = new Boss("Queen Of Hearts", 1500, Witch.class);
	public static Boss ICE_KING = new Boss("Ice King", 2000, Snowman.class);
	
	public void register() {
		
		QUEEN_OF_HEARTS.addAbility(new Blackjack(QUEEN_OF_HEARTS));
		
		WorldEvents.getInstance().getEventManager().registerEvent(QUEEN_OF_HEARTS);
		WorldEvents.getInstance().getEventManager().registerEvent(ICE_KING);
		
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onHealthUpdate(BossUpdateHealthEvent e) {
		ChatColor healthColor = null;
		
		if (e.getHealthTo() < 1000)
			healthColor = ChatColor.RED;
		if (e.getHealthTo() < 100)
			healthColor = ChatColor.DARK_RED;
		if (healthColor != null)
			e.getBoss().setHealthTagFormat(new String("&f%name% &f(%s%health%&f/%s%maxhealth%&f)").replaceAll("%s", healthColor.toString()));
	}
	
	/*
	 * PARTICLES AROUND BOSSES
	 */
	@EventHandler
	public void onBossSpawnParticles(BossSpawnEvent e) {
		
		BukkitRunnable runnable = null;
		double interval = 1D;
		
		Boss boss = e.getBoss();
		
		/*
		 * QUEEN OF HEARTS
		 */
		if (boss == QUEEN_OF_HEARTS) {
			
			interval = 0.2D;
			final int angleRotationInterval = 30;
			runnable = new BukkitRunnable() {
			
				int angleRotation = 0;
				
				@Override
				public void run() {
					if (!boss.isAlive()) {
						this.cancel();
						return;
					}
					
					ColoredParticle[] colors = { new ColoredParticle(214, 48, 49), new ColoredParticle(45, 52, 54) };
					
					double radius = 3D; /* Radius away from the boss */
					int pointsCount = 8; /* Particle pillar count around the witch */
					
					 /* Getting particle pillar locations, based on our rotation */
					ArrayList<Location> locations = new ArrayList<>(); 
					for (int degree = angleRotation; degree < angleRotation + 360; degree += 360/pointsCount) {
						double x = UtilBlock.getXZCordsFromDegree(boss.getAlive().getLocation(), radius, degree)[0];
						double z = UtilBlock.getXZCordsFromDegree(boss.getAlive().getLocation(), radius, degree)[1];
						
						locations.add(new Location(boss.getAlive().getWorld(), x, boss.getAlive().getLocation().clone().getY(), z));
					}
					
					Iterator<Location> points = locations.iterator();
					int index = 0;
					while(points.hasNext()) {
						Location ref = points.next().clone(); /* Point inside the circle */
						ColoredParticle particle = colors[index];
						
						if(index < colors.length-1)
							index++;
						else
							index = 0;
						
						double heightLimit = 3, heightInterval = 0.1;
						for(double i = 0; i < heightLimit; i += heightInterval) {
							Location toSpawn = ref.clone(); /* Location cloned, to edit */
							toSpawn.setY(toSpawn.getY() + i); /* Adding height interval */
							particle.play(toSpawn); 
						}
					}
					
					angleRotation += angleRotationInterval;
					if(angleRotation > 360) angleRotation = 0;
				}
				
			};
			
		/*
		 * ICE KING
		 */
		} else if (boss == ICE_KING) {
			// TODO: Ice King particles
		}

		if(runnable != null) runnable.runTaskTimerAsynchronously(Main.getInstance(), 0, (int) Math.round(20 * interval));
		
		/*
		 * Abilities Handler Startup
		 */
		BossAbilitiesHandler handler = new BossAbilitiesHandler(boss, 60L, 50D);
		handler.start();
	}
	
}

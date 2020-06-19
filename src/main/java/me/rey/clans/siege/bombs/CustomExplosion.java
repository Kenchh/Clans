package me.rey.clans.siege.bombs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.events.clans.PlayerEditClaimEvent;
import me.rey.clans.events.clans.PlayerEditClaimEvent.ClaimPermission;
import me.rey.clans.items.Placeable;
import me.rey.clans.siege.Siege;
import me.rey.clans.siege.SiegeTriggerEvent;
import me.rey.core.effects.ParticleEffect;
import me.rey.core.effects.SoundEffect;
import me.rey.core.utils.UtilBlock;
import me.rey.core.utils.UtilEnt;

public class CustomExplosion implements Listener {
	
	public static HashMap<UUID, HashMap<UUID, Explodable>> ACTIVE_BOMBS = new HashMap<>();
	
	public static Bomb BOMB;
	public static Bomb2 C4;

	public static final double C4_RADIUS = 4D;
	public static final double BOMB_RADIUS = 5D;
	
	private Set<Material> BYPASS_BLOCKS = new HashSet<>(Arrays.asList(
			Material.BEACON,
			Material.CHEST,
			Material.TRAPPED_CHEST,
			Material.FURNACE,
			Material.DISPENSER
			));
	
	private String cause;
	private double damage, radius;
	private boolean instant;
	
	public CustomExplosion(String cause, double damage, boolean instant, double radius) {
		this.cause = cause;
		this.damage = damage;
		this.radius = radius;
		this.instant = instant;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}

	@SuppressWarnings("deprecation")
	public void blow(Clan clan, Player placer, Location location, Block[] toRemove) {
		if (Explodable.isInSiegerTerritory(clan, location) == null) return;
		
		for (Block b : toRemove)
			b.setType(Material.AIR);
		
		/*
		 * EXPLOSION
		 */
		HashMap<Block, Double> blocks = UtilBlock.getBlocksInRadius(location, radius);
		for (Block b : blocks.keySet()) {
			
			if(Bomb2.unbreakable.contains(b) || BYPASS_BLOCKS.contains(b.getType()) || Bomb.unbreakable.contains(b)) continue;
			
			if(b.getType().equals(Material.SMOOTH_BRICK) && !instant && b.getData() != (byte) 2)
				b.setData((byte) 2);
			else
				b.setType(Material.AIR);
		}
		
		Iterator<Entity> ents = UtilBlock.getEntitiesInCircle(location, radius).iterator();
		while(ents.hasNext()) {
			
			Entity next = ents.next();
			
			if(!(next instanceof LivingEntity)) continue;
			
			LivingEntity ent = (LivingEntity) next;
			
			kb(ent, location);
			UtilEnt.damage(damage, cause, ent, placer);
		}

		new ParticleEffect(Effect.EXPLOSION_LARGE).play(location);
		new SoundEffect(Sound.EXPLODE, 2F).play(location);
		
		HashMap<UUID, Explodable> active = CustomExplosion.getActiveBombs(clan);
		active.remove(Explodable.isInSiegerTerritory(clan, location).getUniqueId());
		if(!active.isEmpty())
			CustomExplosion.ACTIVE_BOMBS.put(clan.getUniqueId(), active);
		else
			CustomExplosion.ACTIVE_BOMBS.remove(clan.getUniqueId());
	}
	
	private void kb(LivingEntity ent, Location from) {
		Vector vec = ent.getLocation().toVector().subtract(from.toVector());
		vec.multiply(0.2F);
		
		ent.setVelocity(vec.normalize());
	}
	
	@EventHandler
	public void onForm(EntityChangeBlockEvent e) {
		if(e.getEntity().getType().equals(EntityType.FALLING_BLOCK))
			e.getBlock().setType(Material.AIR);
	}
	
	@EventHandler
	public void onExplode(BlockExplodeEvent e) {
		if(Bomb2.unbreakable.contains(e.getBlock()) || Bomb.unbreakable.contains(e.getBlock()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPrime(ExplosionPrimeEvent e) {
		if(e.getEntity() instanceof TNTPrimed)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		e.setYield(0F);
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.TNT) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			e.setCancelled(true);
	}
	
	public static HashMap<UUID, Explodable> getActiveBombs(Clan clan){
		return ACTIVE_BOMBS.containsKey(clan.getUniqueId()) ? ACTIVE_BOMBS.get(clan.getUniqueId()) : new HashMap<>();
	}
	
	public static abstract class Explodable extends Placeable {
		
		private Clan onTerritory = null, whoPlaced = null;
		
		public Clan getClanOnTerritory() {
			return onTerritory;
		}
		
		public Clan getClanWhoPlaced() {
			return whoPlaced;
		}
		
		public abstract void start(Player player, Location location);
		
		@Override
		protected void place(Player player, Location location) {
			ClansPlayer cp = new ClansPlayer(player);
			if(!cp.hasClan() || !cp.getClan().isSiegingOther()) {
				cp.sendMessageWithPrefix("Siege", "You aren't sieging anyone!");
				return;
			}
			
			if(isInSiegerTerritory(cp.getClan(), location) == null) {
				cp.sendMessageWithPrefix("Siege", "You can only place TNT in territories from clans you're sieging!");
				return;
			}
			
			onTerritory = Main.getInstance().getClanFromTerritory(location.getChunk());
			whoPlaced = cp.getClan();
			
			this.start(player, location);
			HashMap<UUID, Explodable> active = CustomExplosion.getActiveBombs(cp.getClan());
			active.put(isInSiegerTerritory(cp.getClan(), location).getUniqueId(), this);
			CustomExplosion.ACTIVE_BOMBS.put(cp.getClan().getUniqueId(), active);
			
		}

		public static Clan isInSiegerTerritory(Clan sieger, Location location) {
			
			Clan clan = Main.getInstance().getClanFromTerritory(location.getChunk());
			if(clan == null) return null;
			
			for (Siege siege : sieger.getClansSiegedBySelf())
				if (siege.getClanSieged().compare(clan))
					return siege.getClanSieged();
			
			return null;
		}
		
		
		@EventHandler
		public void onEditClaim(PlayerEditClaimEvent e) {
			if(!SiegeTriggerEvent.isInSiegerTerritory(e.getPlayer(), e.getBlockToReplace())) return;
			
			String name = e.getItemInHand() != null && e.getItemInHand().get().hasItemMeta() && e.getItemInHand().get().getItemMeta().hasDisplayName()
					? e.getItemInHand().get().getItemMeta().getDisplayName() 
					: "N/A";
			if(!getItem().getName().equals(name)) return;
			
			e.setPermission(ClaimPermission.ALLOW);
		}
		
		
	}
	
}

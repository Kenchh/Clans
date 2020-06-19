package me.rey.clans.siege.bombs;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.gui.Gui.Item;
import me.rey.clans.items.Placeable;
import me.rey.clans.siege.bombs.CustomExplosion.Explodable;
import me.rey.core.effects.SoundEffect;
import me.rey.core.utils.ChargingBar;

public class Bomb extends Explodable {
	
	public static Set<Block> unbreakable = new HashSet<>();
	
	private static Material[] bombMats = {
			Material.TNT,
			Material.COAL_BLOCK
	};
	
	private static boolean overrideBlockDura = false;
	private static double BombSeconds = 30;
	
	private CustomExplosion customExplosion = new CustomExplosion("C4", 10D, overrideBlockDura, CustomExplosion.BOMB_RADIUS);
	
	@Override
	public Item getItem() {
		return new Item(Material.TNT)
				.setName("&4BOMB");
	}
	
	@Override
	public void start(Player player, Location location) {
		
		Location explosion = location.getBlock().getLocation().clone().add(0.5, 0, 0.5);
		
		final int bars = 10;
		NameTag tag = new NameTag(new ChargingBar(bars, 0).getBarString());
		tag.spawn(explosion.clone().add(0, 0, 0));
		
		unbreakable.add(location.getBlock());
		
		new BukkitRunnable() {
			int matIndex = 0;
			int ticks = 0;
			
			@Override
			public void run() {
				
				if (ticks / 20 >= BombSeconds) {
					Block[] blocks = {location.getBlock()};
					customExplosion.blow(getClanWhoPlaced(), player, explosion, blocks);
					tag.kill();
					
					unbreakable.remove(location.getBlock());
					
					this.cancel();
					return;
				}
				
				if (ticks % 20 == 0) {
					new SoundEffect(Sound.EXPLODE, 1.5F).setVolume(0.1F).play(explosion);
					
					tag.setName(new ChargingBar(bars, (ticks / 20) * 100D / BombSeconds).getBarString());
					
					location.getBlock().setType(bombMats[matIndex]);
					matIndex = matIndex + 1 <= bombMats.length-1 ? matIndex + 1 : 0;
				
				}

				ticks++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
		
	}
	
	@EventHandler
	public void onRemove(BlockBreakEvent e) {
		if(unbreakable.contains(e.getBlock()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent e) {
		if(unbreakable.contains(e.getBlock()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onExplode(BlockExplodeEvent e) {
		if(unbreakable.contains(e.getBlock()))
			e.setCancelled(true);
	}
	
}

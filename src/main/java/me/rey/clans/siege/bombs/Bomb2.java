package me.rey.clans.siege.bombs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.gui.Gui.Item;
import me.rey.clans.siege.bombs.CustomExplosion.Explodable;
import me.rey.core.effects.ParticleEffect;
import me.rey.core.effects.SoundEffect;
import me.rey.core.utils.ChargingBar;
import me.rey.core.utils.UtilBlock;

public class Bomb2 extends Explodable {
	
	public static Set<Block> unbreakable = new HashSet<>();
	
	private static Material[] bombMats = {
			Material.TNT,
			Material.COAL_BLOCK
	};
	
	private static boolean overrideBlockDura = true;
	private static double BombSeconds = 60;
	
	private CustomExplosion customExplosion = new CustomExplosion("C4", 10D, overrideBlockDura, CustomExplosion.C4_RADIUS);
	
	@Override
	public Item getItem() {
		return new Item(Material.TNT)
				.setName("&4C4")
				.setGlow(true);
	}
	
	@Override
	public void start(Player player, Location location) {
	
		BlockFace direction = getCardinalDirection(player).name().toUpperCase().endsWith("_WEST")
				|| getCardinalDirection(player).name().toUpperCase().endsWith("_EAST")
				? getCardinalDirection(player, 135)
				: getCardinalDirection(player);
		BlockFace direction2 = getCardinalDirection(player, 45);
		BlockFace direction3 = getCardinalDirection(player, 90);
		
		Block[] found = {
				location.getBlock(),
				location.getBlock().getRelative(BlockFace.UP),
				location.getBlock().getRelative(direction),
				location.getBlock().getRelative(direction).getRelative(BlockFace.UP),
				location.getBlock().getRelative(direction2),
				location.getBlock().getRelative(direction2).getRelative(BlockFace.UP),
				location.getBlock().getRelative(direction3),
				location.getBlock().getRelative(direction3).getRelative(BlockFace.UP),
		};
		
		location.getBlock().setType(Material.AIR);
		List<Block> air = new ArrayList<>();
		for(int i = 0; i < found.length; i++)
			if(UtilBlock.airFoliage(found[i]))
				air.add(found[i]);
		
		
		Location explosion = UtilBlock.getMidPoint(found[0].getLocation().clone().add(0.5, 0, 0.5), found[5].getLocation().clone().add(0.5, 0, 0.5));
		
		final int bars = 10;
		NameTag tag = new NameTag(new ChargingBar(bars, 0).getBarString());
		tag.spawn(explosion.clone().add(0, 0.5, 0));
		
		new BukkitRunnable() {
			int matIndex = 0;
			int ticks = 0;
			
			Block[] tntBlocks = null;
			
			@Override
			public void run() {
				if(tntBlocks == null) {
					tntBlocks = new Block[air.size()];
					tntBlocks = air.toArray(tntBlocks);
				}
				
				if (ticks / 20 >= BombSeconds) {
					customExplosion.blow(getClanWhoPlaced(), player, explosion, tntBlocks);
					tag.kill();
					
					for(Block b : found) unbreakable.remove(b);
					
					this.cancel();
					return;
				}
				
				if (ticks % 20 == 0) {
					new SoundEffect(Sound.EXPLODE, 1F).setVolume(0.3F).play(explosion);
					
					tag.setName(new ChargingBar(bars, (ticks / 20) * 100D / BombSeconds).getBarString());
					
					changeBlocks(tntBlocks, bombMats[matIndex]);
					matIndex = matIndex + 1 <= bombMats.length-1 ? matIndex + 1 : 0;
					
					List<Block> blocks = new ArrayList<>(UtilBlock.getBlocksInRadius(explosion, 5D, 2D).keySet());
					final int loopCount = 8;
					
					for(int i = 0; i < loopCount; i++) {
						Block b = blocks.get(new Random().nextInt(blocks.size()));
						new ParticleEffect(Effect.SMOKE).play(b.getLocation().clone().add(0.5, 0, 0.5));
					}
				
				}

				ticks++;
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
		
	}
	
	private void changeBlocks(Block[] blocks, Material material) {
		for(Block b : blocks) {
			unbreakable.add(b);
			b.setType(material);
		}
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
	
	private BlockFace getCardinalDirection(Player player, double offset) {
		double rotation = (player.getLocation().getYaw() - 180 + offset) % 360;
		if(rotation < 0) rotation += 360;
		
		if(0 <= rotation && rotation < 22.5) return BlockFace.NORTH;
		else if(22.5 <= rotation && rotation < 67.5) return BlockFace.NORTH_EAST;
		else if(67.5 <= rotation && rotation < 112.5) return BlockFace.EAST;
		else if(112.5 <= rotation && rotation < 157.5) return BlockFace.SOUTH_EAST;
		else if(157.5 <= rotation && rotation < 202.5) return BlockFace.SOUTH;
		else if(202.5 <= rotation && rotation < 247.5) return BlockFace.SOUTH_WEST;
		else if(247.5 <= rotation && rotation < 292.5) return BlockFace.WEST;
		else if(292.5 <= rotation && rotation < 337.5) return BlockFace.NORTH_WEST;
		else if(337.5 <= rotation && rotation < 360) return BlockFace.NORTH;
		
		return BlockFace.NORTH;
	}
	
	private BlockFace getCardinalDirection(Player player) {
		return getCardinalDirection(player, 0);
	}
}

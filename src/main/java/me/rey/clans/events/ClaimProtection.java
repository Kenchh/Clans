package me.rey.clans.events;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.base.Claim;
import me.rey.clans.events.clans.PlayerEditClaimEvent;
import me.rey.clans.events.clans.PlayerEditClaimEvent.ClaimPermission;
import me.rey.clans.events.clans.PlayerEditClaimEvent.EditAction;
import me.rey.clans.events.custom.ContainerOpenEvent;
import me.rey.clans.utils.ErrorCheck;
import me.rey.clans.utils.References;

public class ClaimProtection implements Listener {

	/*
	 * Events Listened for: - DamageEvent (Custom) - PlayerInteractEvent -
	 * PlayerBedEnterEvent - PlayerBucketEvent - BlockBreakEvent - BlockPlaceEvent -
	 * BlockPistonEvent - BlockBurnEvent - LeavesDecayEvent - WeatherChangeEvent -
	 * EnchantItemEvent - PrepareItemEnchantEvent - PlayerFishEvent
	 */
	
	public static List<Material> containers = Arrays.asList(Material.DISPENSER, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.DROPPER, Material.HOPPER, Material.ANVIL);

	public static List<Material> interactables = Arrays.asList(
			/* Fence Gates */ 	Material.FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE,
			/* Doors */ 		Material.WOODEN_DOOR, Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR,
			/* Etc. */			Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.TRAP_DOOR, Material.LEVER,
			/* Redstone */		Material.REDSTONE_COMPARATOR, Material.REDSTONE_COMPARATOR_ON, Material.REDSTONE_COMPARATOR_OFF, Material.DIODE, Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF,
			/* Press. Plates */	Material.WOOD_PLATE, Material.STONE_PLATE, Material.IRON_PLATE, Material.GOLD_PLATE);

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		for(Set<Block> set : Claim.fakeBlocks) {
			if(e.getClickedBlock() == null) break;
			if(set.contains(e.getClickedBlock())) {
				
				for(Player online : Bukkit.getOnlinePlayers())
					for(Block b : set)
						online.sendBlockChange(b.getLocation(), b.getType(), (byte) b.getData());
				
				Claim.fakeBlocks.remove(set);
				break;
			}
		}

		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(References.HOME_BLOCK)) e.setCancelled(true);
		Block clicked = e.getClickedBlock();

		if (isInOtherClaim(e.getPlayer(), clicked) != null) {

			if(containers.contains(clicked.getType()) || interactables.contains(clicked.getType())) {
				ContainerOpenEvent event = new ContainerOpenEvent(e.getPlayer(), clicked, false);
				Bukkit.getServer().getPluginManager().callEvent(event);
				if(event.isAllowed())
					return;
			}

			Clan found = isInOtherClaim(e.getPlayer(), clicked);
			
			PlayerEditClaimEvent event = new PlayerEditClaimEvent(found, e.getPlayer(), ClaimPermission.DENY, EditAction.PLACE, e.getPlayer().getItemInHand(), e.getClickedBlock());
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			if(event.getPermission().equals(ClaimPermission.ALLOW))
				return;
			
			ErrorCheck.noPermissionInClaim(e.getPlayer(), found);
			e.setCancelled(true);
			return;
			
		} else {

			// IRON DOOR
			if (clicked.getType().equals(Material.IRON_DOOR_BLOCK)) {
				BlockState blockState = clicked.getState();
				if(((Door) blockState.getData()).isTopHalf()){
					blockState = clicked.getRelative(BlockFace.DOWN).getState();
				}

				Openable openable = (Openable) blockState.getData();
				openable.setOpen(!openable.isOpen());
				blockState.setData((MaterialData) openable);

				blockState.update();
				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.DOOR_OPEN, 5L, 1F);
				return;
			}

			// IRON TRAP DOOR
			if (clicked.getType().equals(Material.IRON_TRAPDOOR)) {
				BlockState blockState = clicked.getState();

				Openable openable = (Openable) blockState.getData();
				openable.setOpen(!openable.isOpen());
				blockState.setData((MaterialData) openable);

				blockState.update();
				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.DOOR_OPEN, 5L, 1F);
				return;
			}
			
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock() == null || e.getBlock().getType().equals(Material.AIR))
			return;

		Block broken = e.getBlock();

		if (this.isInAClaim(broken) == null)
			return;

		Clan other = this.isInAClaim(broken);
		ClansPlayer self = new ClansPlayer(e.getPlayer());

		/*
		 * checking if it's a clan home
		 */
		if (e.getBlock().getType().equals(References.HOME_BLOCK) && other.getHome() != null) {
			int x = other.getHome().getBlockX(), y = other.getHome().getBlockY(), z = other.getHome().getBlockZ();

			if (broken.getX() == x && broken.getY() == y && broken.getZ() == z) {

				if (self.hasClan() && self.getClan().compare(this.isInAClaim(broken)) && self.getClan()
						.getPlayerRank(e.getPlayer().getUniqueId()).getPower() < ClansRank.ADMIN.getPower()) {
					ErrorCheck.incorrectRank(self.getPlayer(), ClansRank.ADMIN);
					e.setCancelled(true);
					return;
				}

				self.sendMessageWithPrefix("Clan", "You broke the Clan home of &s" + other.getName() + "&r.");
				broken.setType(Material.AIR);
				other.setHome(null);
				Main.getInstance().getSQLManager().saveClan(other);
				return;
			}
		}

		if (self.hasClan() && self.getClan().compare(other))
			return;
		
		PlayerEditClaimEvent event = new PlayerEditClaimEvent(isNearOtherClaim(e.getPlayer(), e.getBlock()), e.getPlayer(), ClaimPermission.DENY, EditAction.BREAK, e.getPlayer().getItemInHand(), e.getBlock());
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if(event.getPermission().equals(ClaimPermission.ALLOW))
			return;
		
		ErrorCheck.noPermissionInClaim(e.getPlayer(), other);
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getBlock() == null || e.getBlock().getType().equals(Material.AIR))
			return;

		if (isNearOtherClaim(e.getPlayer(), e.getBlock()) != null) {
			
			PlayerEditClaimEvent event = new PlayerEditClaimEvent(isNearOtherClaim(e.getPlayer(), e.getBlock()),
					e.getPlayer(), ClaimPermission.DENY, EditAction.PLACE, e.getPlayer().getItemInHand(), e.getBlock());
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			if(event.getPermission().equals(ClaimPermission.ALLOW))
				return;
			
			ErrorCheck.noPermissionNearClaim(e.getPlayer(), isNearOtherClaim(e.getPlayer(), e.getBlock()));
			e.setCancelled(true);
			return;
		}

		/*
		 * CHECK if they placed INSIDE claim
		 */
//		Block placed = e.getBlock();
//		if (this.isInOtherClaim(e.getPlayer(), placed) == null)
//			return;
//
//		ErrorCheck.noPermissionInClaim(e.getPlayer(), this.isInOtherClaim(e.getPlayer(), placed));
//		e.setCancelled(true);
	}

	@EventHandler
	public void fishEvent(PlayerFishEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void prepareEnchant(PrepareItemEnchantEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void enchantItem(EnchantItemEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void weatherEvent(WeatherChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void leavesDecay(LeavesDecayEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void blockBurnEvent(BlockBurnEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void playerBucketEvent(PlayerBucketEmptyEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void playerBucketFill(PlayerBucketFillEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void bedEnter(PlayerBedEnterEvent e) {
		e.setCancelled(true);
	}
	
	public static Clan isNearOtherClaim(Player player, Block block) {
		ClansPlayer cp = new ClansPlayer(player);
		
		World w = block.getWorld();
		Block[] sides = new Block[8];
		int x = block.getX(), y = block.getY(), z = block.getZ();
		sides[0] = w.getBlockAt(new Location(w, x - 1, y, z));
		sides[1] = w.getBlockAt(new Location(w, x + 1, y, z)); // sides
		sides[2] = w.getBlockAt(new Location(w, x, y, z - 1));
		sides[3] = w.getBlockAt(new Location(w, x, y, z + 1)); // sides
		sides[4] = w.getBlockAt(new Location(w, x + 1, y, z + 1));
		sides[5] = w.getBlockAt(new Location(w, x + 1, y, z - 1)); // corners
		sides[6] = w.getBlockAt(new Location(w, x - 1, y, z + 1));
		sides[7] = w.getBlockAt(new Location(w, x - 1, y, z - 1)); // corners

		for (Block b : sides) {
			Chunk chunk = b.getChunk();
			if (chunk.equals(block.getChunk()))
				continue;
			if (isInClaim(chunk) == null)
				continue;

			Clan other = isInClaim(chunk);
			if (cp.hasClan() && other.compare(cp.getClan()))
				continue;
		
			return other;
		}
		return null;
	}

	public static Clan isInOtherClaim(Player player, Block block) {
		Clan owner = Main.getInstance().getClanFromTerritory(block.getChunk());
		ClansPlayer self = new ClansPlayer(player);
		return owner == null ? null : (self.hasClan() && self.getClan().compare(owner) ? null : owner);
	}

	public static Clan isInClaim(Chunk chunk) {
		return Main.getInstance().getClanFromTerritory(chunk);
	}

	public Clan isInAClaim(Block block) {
		return isInClaim(block.getChunk());
	}
	
}

package me.rey.clans.events;

import java.util.Arrays;
import java.util.List;

import me.rey.clans.commands.base.Claim;
import org.bukkit.*;
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

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.events.custom.ContainerOpenEvent;
import me.rey.clans.utils.ErrorCheck;
import me.rey.clans.utils.References;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.material.TrapDoor;

public class ClaimProtection implements Listener {

	/*
	 * Events Listened for: - DamageEvent (Custom) - PlayerInteractEvent -
	 * PlayerBedEnterEvent - PlayerBucketEvent - BlockBreakEvent - BlockPlaceEvent -
	 * BlockPistonEvent - BlockBurnEvent - LeavesDecayEvent - WeatherChangeEvent -
	 * EnchantItemEvent - PrepareItemEnchantEvent - PlayerFishEvent
	 */
	
	List<Material> containers = Arrays.asList(Material.DISPENSER, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.DROPPER, Material.HOPPER, Material.ANVIL);

	List<Material> interactables = Arrays.asList(
			/* Fence Gates */ 	Material.FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE,
			/* Doors */ 		Material.WOODEN_DOOR, Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR,
			/* Etc. */			Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.TRAP_DOOR, Material.LEVER,
			/* Redstone */		Material.REDSTONE_COMPARATOR, Material.REDSTONE_COMPARATOR_ON, Material.REDSTONE_COMPARATOR_OFF, Material.DIODE, Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF,
			/* Press. Plates */	Material.WOOD_PLATE, Material.STONE_PLATE, Material.IRON_PLATE, Material.GOLD_PLATE);

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {

		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(References.HOME_BLOCK)) e.setCancelled(true);
		Block clicked = e.getClickedBlock();

		if (this.isInOtherClaim(e.getPlayer(), clicked) != null) {

			if(containers.contains(clicked.getType()) || interactables.contains(clicked.getType())) {
				ContainerOpenEvent event = new ContainerOpenEvent(e.getPlayer(), clicked, false);
				Bukkit.getServer().getPluginManager().callEvent(event);
				if(event.isAllowed())
					return;
			} else {
				return;
			}

			ErrorCheck.noPermissionInClaim(e.getPlayer(), this.isInOtherClaim(e.getPlayer(), clicked));
			e.setCancelled(true);
			return;
		}

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

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock() == null || e.getBlock().getType().equals(Material.AIR))
			return;

		Block broken = e.getBlock();

		if(broken.getType() == Material.SEA_LANTERN) {
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			Claim.resetDrawnBorders(broken.getChunk(), e.getPlayer());
		}

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
		ErrorCheck.noPermissionInClaim(e.getPlayer(), other);
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (e.getBlock() == null || e.getBlock().getType().equals(Material.AIR))
			return;

		ClansPlayer cp = new ClansPlayer(e.getPlayer());

		/*
		 * Check if they placed block NEXT to a claim
		 */
		World w = e.getBlock().getWorld();
		Block[] sides = new Block[8];
		int x = e.getBlock().getX(), y = e.getBlock().getY(), z = e.getBlock().getZ();
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
			if (chunk.equals(e.getBlock().getChunk()))
				continue;
			if (isInClaim(chunk) == null)
				continue;

			Clan other = isInClaim(chunk);
			if (cp.hasClan() && other.compare(cp.getClan()))
				continue;

			ErrorCheck.noPermissionNearClaim(e.getPlayer(), other);
			e.setCancelled(true);
			break;
		}
		// END

		/*
		 * CHECK if they placed INSIDE claim
		 */
		Block placed = e.getBlock();
		if (this.isInOtherClaim(e.getPlayer(), placed) == null)
			return;

		ErrorCheck.noPermissionInClaim(e.getPlayer(), this.isInOtherClaim(e.getPlayer(), placed));
		e.setCancelled(true);
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

	private Clan isInOtherClaim(Player player, Block block) {
		Clan owner = Main.getInstance().getClanFromTerritory(block.getChunk());
		ClansPlayer self = new ClansPlayer(player);
		return owner == null ? null : (self.hasClan() && self.getClan().compare(owner) ? null : owner);
	}

	private Clan isInClaim(Chunk chunk) {
		return Main.getInstance().getClanFromTerritory(chunk);
	}

	public Clan isInAClaim(Block block) {
		return isInClaim(block.getChunk());
	}
	
}

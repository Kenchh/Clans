package me.rey.clans.events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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

public class ClaimProtection implements Listener {

	/*
	 * Events Listened for: - DamageEvent (Custom) - PlayerInteractEvent -
	 * PlayerBedEnterEvent - PlayerBucketEvent - BlockBreakEvent - BlockPlaceEvent -
	 * BlockPistonEvent - BlockBurnEvent - LeavesDecayEvent - WeatherChangeEvent -
	 * EnchantItemEvent - PrepareItemEnchantEvent - PlayerFishEvent
	 */
	
	List<Material> containers = Arrays.asList(Material.DISPENSER, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.DROPPER, Material.HOPPER);

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(References.HOME_BLOCK)) e.setCancelled(true);

		Block clicked = e.getClickedBlock();

		if (this.isInOtherClaim(e.getPlayer(), clicked) != null) {


			if(containers.contains(clicked.getType())) {
				ContainerOpenEvent event = new ContainerOpenEvent(e.getPlayer(), clicked, false);
				Bukkit.getServer().getPluginManager().callEvent(event);
				if(event.isAllowed())
					return;
			}
			
			ErrorCheck.noPermissionInClaim(e.getPlayer(), this.isInOtherClaim(e.getPlayer(), clicked));
			e.setCancelled(true);
			return;
		}

		if (clicked.getType().equals(Material.IRON_DOOR) || clicked.getType().equals(Material.IRON_DOOR_BLOCK)) {
			// TODO: Open iron doors
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

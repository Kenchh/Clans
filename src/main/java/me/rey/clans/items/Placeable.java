package me.rey.clans.items;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.rey.clans.gui.Gui.Item;
import me.rey.core.Warriors;

public abstract class Placeable implements Listener {
	
	public Placeable() {
		Bukkit.getServer().getPluginManager().registerEvents(this,  Warriors.getInstance());
	}
	
	public abstract Item getItem();
	protected abstract void place(Player player, Location location);
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		String name = e.getItemInHand() != null && e.getItemInHand().hasItemMeta() && e.getItemInHand().getItemMeta().hasDisplayName()
				? e.getItemInHand().getItemMeta().getDisplayName() 
				: "N/A";
				
		if(this.getItem().getName().equals(name)) {
			e.setCancelled(true);
			this.place(e.getPlayer(), e.getBlockPlaced().getLocation());
			
			if (e.getPlayer().getItemInHand().getAmount() <= 1)
				e.getPlayer().setItemInHand(new Item(Material.AIR).get());
			else
				e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
		}
	}

}

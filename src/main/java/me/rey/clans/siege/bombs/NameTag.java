package me.rey.clans.siege.bombs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import me.rey.clans.Main;

public class NameTag implements Listener {
	
	private ArmorStand armorStand = null;
	private boolean spawned = false;
	
	private String name;
	
	public NameTag(String text) {
		this.name = text;
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	public NameTag setName(String text) {
		this.name = text;
		
		if(spawned)
			armorStand.setCustomName(name);
		return this;
	}
	
	public void spawn(Location location) {
		if(spawned) return;
		
		ArmorStand as1 = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as1.setGravity(false);
        as1.setCanPickupItems(true);
        as1.setCustomName(name);
        as1.setCustomNameVisible(true);
        as1.setVisible(false);
        
        spawned = true;
        this.armorStand = as1;
	}
	
	public void kill() {
		if(spawned) {
			armorStand.remove();
			armorStand = null;
			spawned = false;
		}
	}
	
	public void move(Location location) {
		if(!spawned) return;
		
		armorStand.teleport(location);
	}
	
	@EventHandler
	public void onEntityDeath(EntityExplodeEvent e) {
		if(e.getEntity().equals(armorStand))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntity().equals(armorStand))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onArmorStand(PlayerArmorStandManipulateEvent e) {
		if(e.getRightClicked().equals(armorStand))
			e.setCancelled(true);
	}
	

}

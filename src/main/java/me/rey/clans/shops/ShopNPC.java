package me.rey.clans.shops;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.rey.clans.Main;
import me.rey.clans.gui.Gui;
import me.rey.clans.packets.Freeze;
import me.rey.clans.utils.Text;
import me.rey.parser.Parser;

public class ShopNPC implements Listener {
	
	private int id;
	private String NPC_NAME;
	private Gui GUI;
	private Parser parser;
	
	public ShopNPC(int id, String npcName, Gui guiToOpen, Parser parser) {
		this.id = id;
		this.NPC_NAME = Text.color(npcName);
		this.GUI = guiToOpen;
		this.parser = parser;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	public int getId() {
		return id;
	}
	
	public Parser getParser() {
		return parser;
	}
	
	public void open(Player player) {
		GUI.open(player);
	}
	
	public void spawn(Location location) {
		Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
		villager.setProfession(Profession.PRIEST);
		villager.setCanPickupItems(false);
		
		
		/*
		 * NAME
		 */
		ArmorStand as1 = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        as1.setGravity(false);
        as1.setCanPickupItems(true);
        as1.setCustomName(Text.color(this.NPC_NAME));
        as1.setCustomNameVisible(true);
        as1.setVisible(false);
		
		new Freeze().send(villager);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if(e.getRightClicked().getType() != EntityType.VILLAGER) return;
		if(!matches(e.getRightClicked())) return;
		
		e.setCancelled(true);
		this.open(e.getPlayer());
	}
	
	@EventHandler
	public void onNPCDamage(EntityDamageEvent e) {
		if(e.getEntity().getType() != EntityType.VILLAGER) return;
		if(!matches(e.getEntity())) return;
		
		e.setCancelled(true);
	}
	
	private boolean matches(Entity villager) {
		if(villager.getNearbyEntities(0, 0, 0).isEmpty()) return false;
		Iterator<Entity> entities = villager.getNearbyEntities(0, 0, 0).iterator();
		
		while(entities.hasNext()) {
			Entity next = entities.next();
			if(next.getCustomName().equals(Text.color(NPC_NAME)))
				return true;
		}
		return false;
	}
}
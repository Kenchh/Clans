package me.rey.clans.shops.guis;

import org.bukkit.Material;

import me.rey.clans.Main;
import me.rey.clans.gui.Gui;
import me.rey.clans.items.CustomItems;
import me.rey.clans.shops.ShopItem;
import me.rey.core.classes.ClassType;
import me.rey.core.pvp.ToolType;

public class PvP extends Gui {

	public PvP() {
		super("PvP Shop", 6, Main.getInstance());
	}

	@Override
	public void init() {
		int row = 9;
		
		int index = 0;
		for(ClassType type : ClassType.values()) {
			
			Material[] armor = type.getArmor();
			
			this.setItem(new ShopItem(new Item(armor[0]).setName(type.getName() + " Helmet").get(), 2500, 500, true), index); // HELMET
			this.setItem(new ShopItem(new Item(armor[1]).setName(type.getName() + " Chestplate").get(), 4000, 800, true), index + row*1); // CHESTPLATE
			this.setItem(new ShopItem(new Item(armor[2]).setName(type.getName() + " Leggings").get(), 3500, 700, true), index + row*2); // LEGGINGS
			this.setItem(new ShopItem(new Item(armor[3]).setName(type.getName() + " Boots").get(), 2000, 400, true), index + row*3); // BOOTS
			
			index++;
		}
		
		/*
		 * SWORDS
		 */
		ToolType[] swords = {ToolType.BOOSTER_SWORD, ToolType.POWER_SWORD, ToolType.STANDARD_SWORD};
		this.setItem(new ShopItem(swords[0].getItemStack(), 9000, 1800, true), 8); // BOOSTER
		this.setItem(new ShopItem(swords[1].getItemStack(), 9000, 1800, true), 7); // POWER
		this.setItem(new ShopItem(swords[2].getItemStack(), 1000, 200, true), 6); // STANDARD
		
		/*
		 * AXES
		 */
		ToolType[] axes = {ToolType.BOOSTER_AXE, ToolType.POWER_AXE, ToolType.STANDARD_AXE};
		this.setItem(new ShopItem(axes[0].getItemStack(), 9000, 1800, true), 8+row); // BOOSTER
		this.setItem(new ShopItem(axes[1].getItemStack(), 9000, 1800, true), 7+row); // POWER
		this.setItem(new ShopItem(axes[2].getItemStack(), 1000, 200, true), 6+row); // STANDARD
		
		/*
		 * BOW AND ARROWS
		 */
		this.setItem(new ShopItem(ToolType.STANDARD_BOW.getItemStack(), 175, 35, true), 0 + 5*row); // BOW
		this.setItem(new ShopItem(Material.ARROW, 20, 2), 1 + 5*row); // ARROWS
		
		/*
		 * SPECIAL
		 */
		this.setItem(new ShopItem(CustomItems.CLASS_SHOP.get(), 30000, 10000, true), 8 + 5*row); // CLASS SHOP
		this.setItem(CustomItems.getPlaceholder(Material.TNT, "&4TODO: &lBOMB"), 8 + 4*row); // BOMB
		this.setItem(CustomItems.getPlaceholder(Material.TNT, "&4TODO: &lC4"), 7 + 4*row); // C4
		this.setItem(CustomItems.getPlaceholder(Material.TNT, "&4TODO: &lBOMB GENERATOR"), 7 + 5*row); // BOMB GENERATOR
		this.setItem(CustomItems.getPlaceholder(Material.BEACON, "&4TODO: &lOutpost"), 8 + 4*row);
		
	}

}

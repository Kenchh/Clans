package me.rey.clans.items.crafting;

import org.bukkit.Material;

import me.rey.core.pvp.ToolType;

public class PowerAxe extends CraftingRecipe {

	public PowerAxe() {
		super(ToolType.POWER_AXE.getItemStack());
	}

	@Override
	public CraftingRecipe init() {
		this.shape("DD ", "DS ", " S ");
		this.setIngredient('D', Material.DIAMOND_BLOCK);
		this.setIngredient('S', Material.STICK);
		
		return this;
	}

}

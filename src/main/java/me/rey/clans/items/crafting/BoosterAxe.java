package me.rey.clans.items.crafting;

import org.bukkit.Material;

import me.rey.core.pvp.ToolType;

public class BoosterAxe extends CraftingRecipe {

	public BoosterAxe() {
		super(ToolType.BOOSTER_AXE.getItemStack());
	}
	
	@Override
	public CraftingRecipe init() {
		this.shape("GG ", "GS ", " S ");
		this.setIngredient('G', Material.GOLD_BLOCK);
		this.setIngredient('S', Material.STICK);
		
		return this;
	}

}

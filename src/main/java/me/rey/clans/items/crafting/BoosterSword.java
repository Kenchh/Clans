package me.rey.clans.items.crafting;

import org.bukkit.Material;

import me.rey.core.pvp.ToolType;

public class BoosterSword extends CraftingRecipe {
	
	public BoosterSword() {
		super(ToolType.BOOSTER_SWORD.getItemStack());
	}

	@Override
	public CraftingRecipe init() {
		this.shape(" G ", " G ", " S ");
		this.setIngredient('G', Material.GOLD_BLOCK);
		this.setIngredient('S', Material.STICK);
		
		return this;
	}

}

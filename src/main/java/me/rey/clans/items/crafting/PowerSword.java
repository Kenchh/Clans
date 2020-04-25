package me.rey.clans.items.crafting;

import org.bukkit.Material;

import me.rey.core.pvp.ToolType;

public class PowerSword extends CraftingRecipe {

	public PowerSword() {
		super(ToolType.POWER_SWORD.getItemStack());
	}

	@Override
	public CraftingRecipe init() {
		this.shape(" D ", " D ", " S ");
		this.setIngredient('D', Material.DIAMOND_BLOCK);
		this.setIngredient('S', Material.STICK);
		return this;
	}

}

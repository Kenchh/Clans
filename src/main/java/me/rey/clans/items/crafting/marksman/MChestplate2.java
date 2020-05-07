package me.rey.clans.items.crafting.marksman;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.rey.clans.items.crafting.CraftingRecipe;
import me.rey.core.classes.ClassType;

public class MChestplate2 extends CraftingRecipe {

	public MChestplate2() {
		super(new ItemStack(ClassType.CHAIN.getChestplate().get()));
	}

	@Override
	public CraftingRecipe init() {
		this.shape("I I", "GIG", "IGI");
		this.setIngredient('G', Material.GOLD_INGOT);
		this.setIngredient('I', Material.IRON_INGOT);
		
		return this;
	}

}

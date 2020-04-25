package me.rey.clans.items.crafting.marksman;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.rey.clans.items.crafting.CraftingRecipe;
import me.rey.core.classes.ClassType;

public class MChestplate1 extends CraftingRecipe {

	public MChestplate1() {
		super(new ItemStack(ClassType.CHAIN.getChestplate()));
	}

	@Override
	public CraftingRecipe init() {
		this.shape("G G", "IGI", "GIG");
		this.setIngredient('G', Material.GOLD_INGOT);
		this.setIngredient('I', Material.IRON_INGOT);
		
		return this;
	}

}

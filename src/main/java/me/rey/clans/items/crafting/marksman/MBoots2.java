package me.rey.clans.items.crafting.marksman;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.rey.clans.items.crafting.CraftingRecipe;
import me.rey.core.classes.ClassType;

public class MBoots2 extends CraftingRecipe {

	public MBoots2() {
		super(new ItemStack(ClassType.CHAIN.getBoots().get()));
	}

	@Override
	public CraftingRecipe init() {
		this.shape("   ", "G G", "I I");
		this.setIngredient('G', Material.GOLD_INGOT);
		this.setIngredient('I', Material.IRON_INGOT);
		
		return this;
	}
	
}

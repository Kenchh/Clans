package me.rey.clans.items.crafting.marksman;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.rey.clans.items.crafting.CraftingRecipe;
import me.rey.core.classes.ClassType;

public class MHelmet2 extends CraftingRecipe {

	public MHelmet2() {
		super(new ItemStack(ClassType.CHAIN.getHelmet().get()));
	}

	@Override
	public CraftingRecipe init() {
		this.shape("GIG", "I I", "   ");
		this.setIngredient('G', Material.GOLD_INGOT);
		this.setIngredient('I', Material.IRON_INGOT);
		
		return this;
	}
	
}

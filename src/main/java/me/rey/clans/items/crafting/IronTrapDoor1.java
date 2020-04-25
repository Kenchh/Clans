package me.rey.clans.items.crafting;

import org.bukkit.Material;

import me.rey.clans.gui.Gui.Item;

public class IronTrapDoor1 extends CraftingRecipe {

	public IronTrapDoor1() {
		super(new Item(Material.IRON_DOOR).setAmount(1).get());
		this.setIgnoreOldRecipes(true);
	}

	@Override
	public CraftingRecipe init() {
		this.shape("   ", "III", "III");
		this.setIngredient('I', Material.WOOD);
		
		return this;
	}

}

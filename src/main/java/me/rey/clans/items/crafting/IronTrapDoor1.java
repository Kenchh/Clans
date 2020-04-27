package me.rey.clans.items.crafting;

import org.bukkit.Material;

import me.rey.clans.gui.Gui.Item;
import me.rey.clans.items.crafting.CraftingRecipe.IExtraCraft;

public class IronTrapDoor1 extends CraftingRecipe implements IExtraCraft {

	public IronTrapDoor1() {
		super(new Item(Material.IRON_TRAPDOOR).setAmount(1).get());
	}

	@Override
	public CraftingRecipe init() {
		this.shape("   ", " II", " II");
		this.setIngredient('I', Material.WOOD);
		
		return this;
	}

}

package me.rey.clans.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.rey.clans.gui.Gui.GuiItem;
import me.rey.clans.gui.Gui.Item;
import me.rey.core.pvp.ToolType;
import me.rey.core.utils.Text;

public class CustomItems {
	
	public static final Item CLASS_SHOP = new Item(Material.ENCHANTMENT_TABLE).setName("Class Shop");
	
	public static final Item[] CUSTOM_ITEMS = {
			CLASS_SHOP
			};
	
	
	
	public static GuiItem getPlaceholder(Material item, String name) {
		return new GuiItem(new Item(item).setName(name)) {
			@Override
			public void onUse(Player player, ClickType type, int slot) {		}
		};
	}
	
	public static ItemStack updateItem(ItemStack item) {
		Material type = item.getType();
		ItemMeta meta = item.getItemMeta();
		
		for(ToolType tool : ToolType.values()) {
			if(!type.equals(tool.getType())) continue;
			meta.setDisplayName(Text.color(tool.getName()));
			break;
		}
		
		for(Item query : CUSTOM_ITEMS) {
			if(!query.get().getType().equals(type)) continue;
			
			item = query.get();
			break;
		}
		
		item.setItemMeta(meta);
		return item;
	}

}

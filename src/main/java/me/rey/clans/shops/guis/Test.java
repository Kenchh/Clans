package me.rey.clans.shops.guis;

import org.bukkit.Material;

import me.rey.clans.Main;
import me.rey.clans.gui.Gui;
import me.rey.clans.shops.ShopItem;

public class Test extends Gui {

	public Test() {
		super("&9Test", 6, Main.getInstance());
	}

	@Override
	public void init() {
		this.setItem(new ShopItem(Material.STONE, 1000, 2000), 24);
	}

}

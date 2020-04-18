package me.rey.clans.clans;

import org.bukkit.Material;

import me.rey.clans.Main;
import me.rey.clans.utils.Text;
import me.rey.parser.ParseType;
import me.rey.parser.Parser;

public enum ServerClan {
	
	WILDERNESS(),
	BORDERLANDS(new Parser("Borderlands", Material.OBSIDIAN, ParseType.CUBOID, true)),
	SHOPS(new Parser("Shops", Material.HAY_BLOCK, ParseType.CUBOID, true)),
	FIELDS(new Parser("Fields", Material.IRON_ORE, ParseType.CUBOID, true)),
	SPAWN(new Parser("Spawn", Material.DIAMOND_BLOCK, ParseType.CUBOID, true)),
	NETHER(new Parser("Nether", Material.NETHERRACK, ParseType.CUBOID, true));
	
	private Parser parser;
	
	ServerClan(Parser parser){
		this.parser = parser;
	}
	
	ServerClan(){
		this.parser = null;
	}
	
	public Clan getClan() {
		return Main.getInstance().getSQLManager().getServerClan(this);
	}
	
	public Parser getParser() {
		return parser;
	}
	
	public String getName() {
		return Text.formatName(this.name().toLowerCase());
	}

}

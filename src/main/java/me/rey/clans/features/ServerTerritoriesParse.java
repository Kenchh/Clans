package me.rey.clans.features;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ServerClan;
import me.rey.parser.ParseEvent;
import me.rey.parser.ParseType;
import me.rey.parser.Parser;
import me.rey.parser.ParserPlugin;

public class ServerTerritoriesParse implements Listener {

	Parser safeZone = new Parser("Safe", Material.BEDROCK, ParseType.CUBOID, true);
	
	public void init() {
		// safe zone
		ParserPlugin.getInstance().registerParser(safeZone);
		
		// server territories
		for(ServerClan type : ServerClan.values()) {
			if(type.getParser() == null) continue;
			ParserPlugin.getInstance().registerParser(type.getParser());
		}	
	}
	
	
	/*
	 * SERVER SAFE ZONES
	 */
	@EventHandler
	public void onSafeZone(ParseEvent e) {
		if(e.getParser() == null || !e.getParser().getName().equals(safeZone.getName())) return;
		if(e.getParsedPoints().isEmpty()) return;
		Iterator<Block> blocks = e.getParsedPoints().iterator();
		Set<String> coords = new HashSet<String>();
		
		Set<String> clone = Main.getInstance().getSQLManager().getSafeZones();
		while(blocks.hasNext()) {
			Block found = blocks.next();
			String text = String.format("%s;%s;%s", found.getX(), found.getY(), found.getZ());
			if(clone.contains(text)) continue;
			coords.add(text);
		}
		
		Main.getInstance().getSQLManager().saveSafeZones(coords);
		Main.safeZoneCoords = Main.getInstance().getSQLManager().getSafeZones();
	}
	
	/*
	 * SERVER TERRITORIES
	 */
	@EventHandler
	public void onParse(ParseEvent e) {
		if(e.getParser() == null) return;
		
		for(ServerClan type: ServerClan.values()) {
			if(type.getParser() == null) continue;
			
			// matches parser
			if(e.getParser().getName().equals(type.getParser().getName())) {
				if(e.getParsedPoints() == null) continue;
				Iterator<Block> blocks = e.getParsedPoints().iterator();
				
				Set<Chunk> inClaim = new HashSet<Chunk>();
				while(blocks.hasNext()) {
					Block found = blocks.next();
					
					inClaim.add(found.getChunk());
				}
				
				Clan clan = Main.getInstance().getSQLManager().getServerClan(type);
				for(Chunk claimed : inClaim) {
					clan.addTerritory(claimed);
				}
				
				Main.getInstance().getSQLManager().saveClan(clan);
				
				//deleting thsoe blocks
				Iterator<Block> toRemove = e.getParsedPoints().iterator();
				while(toRemove.hasNext()) {
					Block next = toRemove.next();
					if(next.getType().equals(e.getParser().getDataBlock())) {
						next.setType(Material.AIR);

						if(e.getParser().useSponge())
							next.getRelative(BlockFace.DOWN).setType(Material.AIR);
					}
				}
			}
		}
	}
}

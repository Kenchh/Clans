package me.rey.clans.commands.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.events.clans.ClanTerritoryClaimEvent;

public class Claim extends SubCommand {
	
	public static ArrayList<Set<Block>> fakeBlocks = new ArrayList<>();

	public Claim() {
		super("claim", "Claim a piece of land", "/c claim", ClansRank.ADMIN, CommandType.CLAN, true);
	}

	@Override	
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		Player player = (Player) sender;
		Chunk standing = player.getLocation().getChunk();
		
		Clan clan = new ClansPlayer(player).getClan();
		if(clan.hasMaxTerritory()) {
			this.sendMessageWithPrefix("Error", "You already claimed &s" + clan.getTerritory().size() + " &rChunks!");
			return;
		}
		
		if(Main.getInstance().getClanFromTerritory(standing) != null && Main.getInstance().getClanFromTerritory(standing).getUniqueId() != clan.getUniqueId()) {
			this.sendMessageWithPrefix("Error", "This territory is owned by &s" + Main.getInstance().getClanFromTerritory(standing).getName() + "&r.");
			return;
		}

		if(Main.getInstance().getClanFromTerritory(standing) != null && Main.getInstance().getClanFromTerritory(standing).getUniqueId() == clan.getUniqueId()) {
			this.sendMessageWithPrefix("Error", "You have already claimed this territory!");
			return;
		}

		int x = standing.getX(), z = standing.getZ();
		World w = standing.getWorld();
		Chunk[] sides = new Chunk[4], corners = new Chunk[4];
		sides[0] = w.getChunkAt(x-1, z); sides[1] = w.getChunkAt(x+1, z); sides[2] = w.getChunkAt(x, z-1); sides[3] = w.getChunkAt(x, z+1);
		corners[0] = w.getChunkAt(x-1, z-1); corners[1] = w.getChunkAt(x+1, z+1); corners[2] = w.getChunkAt(x-1, z+1); corners[3] = w.getChunkAt(x+1, z+1);
		
		boolean isNextToSelf = clan.getTerritory().isEmpty() ? true : false;
		Clan isNextToOther = null;
		for(Chunk near : sides) {
			if(Main.getInstance().getClanFromTerritory(near) != null) {
				Clan claimed = Main.getInstance().getClanFromTerritory(near);
				if(claimed.compare(clan))
					isNextToSelf = true;
				else
					isNextToOther = claimed;
			}
		}
		
		for(Chunk near : corners) {
			if(Main.getInstance().getClanFromTerritory(near) != null) {
				Clan claimed = Main.getInstance().getClanFromTerritory(near);
				if(!claimed.compare(clan))
					isNextToOther = claimed;
			}
		}
		
		if(isNextToSelf) {
		
			if(isNextToOther != null) {
				this.sendMessageWithPrefix("Error", "You cannot claim next to &s" + isNextToOther.getName() + "&r!");
				return;
			}
			drawBorders(standing, player);
			clan.addTerritory(standing);
			this.sendMessageWithPrefix("Clan", "Successfully claimed chunk (&s" + standing.getX() + "&r, &e" + standing.getZ() + "&r).");
			this.sql().saveClan(clan);
			
			/*
			 * EVENT HANDLING
			 */
			ClanTerritoryClaimEvent event = new ClanTerritoryClaimEvent(clan, player, new ArrayList<Chunk>(Arrays.asList(standing)));
			Bukkit.getServer().getPluginManager().callEvent(event);
		} else {
			this.sendMessageWithPrefix("Error", "You must claim next to your owned territory!");
		}
	}

	@SuppressWarnings("deprecation")
	public void drawBorders(Chunk standing, Player p) {
		Set<Block> toReplace = new HashSet<>();
		
		for(int a=0; a<=15; a++) {
			
			Block blockC = standing.getBlock(a, 0, 0);
			Block highestblock = p.getWorld().getHighestBlockAt(blockC.getX(), blockC.getZ());
			Block block = p.getWorld().getBlockAt(highestblock.getX(), highestblock.getY() - 1, highestblock.getZ());
			
			toReplace.add(block);
		}

		for(int b=0; b<=14; b++) {

			Block blockC = standing.getBlock(0, 0, b + 1);
			Block highestblock = p.getWorld().getHighestBlockAt(blockC.getX(), blockC.getZ());
			Block block = p.getWorld().getBlockAt(highestblock.getX(), highestblock.getY() - 1, highestblock.getZ());
			
			toReplace.add(block);
		}

		for(int c=0; c<=14; c++) {

			Block blockC = standing.getBlock(15, 0, c + 1);
			Block highestblock = p.getWorld().getHighestBlockAt(blockC.getX(), blockC.getZ());
			Block block = p.getWorld().getBlockAt(highestblock.getX(), highestblock.getY() - 1, highestblock.getZ());
		
			toReplace.add(block);
		}

		for(int d=0; d<=13; d++) {

			Block blockC = standing.getBlock(d + 1, 0, 15);
			Block highestblock = p.getWorld().getHighestBlockAt(blockC.getX(), blockC.getZ());
			Block block = p.getWorld().getBlockAt(highestblock.getX(), highestblock.getY() - 1, highestblock.getZ());

			toReplace.add(block);
		}
		
		for(Player online : Bukkit.getOnlinePlayers())
			for(Block b : toReplace)
				online.sendBlockChange(b.getLocation(), Material.SEA_LANTERN, (byte) 0);

		fakeBlocks.add(toReplace);
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

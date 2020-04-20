package me.rey.clans.commands.base;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;

public class Claim extends SubCommand {

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
		
		if(Main.getInstance().getClanFromTerritory(standing) != null) {
			this.sendMessageWithPrefix("Error", "This territory is owned by &s" + Main.getInstance().getClanFromTerritory(standing).getName() + "&r.");
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
			
			clan.addTerritory(standing);
			this.sendMessageWithPrefix("Clan", "Successfully claimed chunk (&s" + standing.getX() + "&r, &e" + standing.getZ() + "&r).");
			this.sql().saveClan(clan);
		} else {
			this.sendMessageWithPrefix("Error", "You must claim next to your owned territory!");
		}
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

package me.rey.clans.commands.base;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
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
import me.rey.clans.utils.References;

public class SetHome extends SubCommand {

	public SetHome() {
		super("sethome", "Set your Clan home", "/c sethome", ClansRank.ADMIN, CommandType.CLAN, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		Clan clan = new ClansPlayer((Player) sender).getClan();
		Player player = (Player) sender;
		
		if(!isInSelfClaim(player, player.getLocation().getBlock())) {
			new ClansPlayer(player).sendMessageWithPrefix("Error", "You are not in your claim!");
			return;
		}
		
		int x = player.getLocation().getBlockX(), y = player.getLocation().getBlockY(), z = player.getLocation().getBlockZ();
		World w = Main.getInstance().getClansWorld();
		List<Block> space = Arrays.asList(player.getLocation().getBlock(), new Location(w, x, y+1, z).getBlock(), new Location(w, x, y+2, z).getBlock());
		
		for(Block block : space) {
			if(block == null || (!block.getType().equals(Material.AIR) && block.getType().isBlock())) {
				new ClansPlayer(player).sendMessageWithPrefix("Error", "This is not a suitable place for a home!");
				return;
			}
		}
		
		player.getLocation().getBlock().setType(References.HOME_BLOCK);
		Location home = new Location(Main.getInstance().getClansWorld(), x, y ,z);
	
		if(clan.getHome() != null && clan.getHome().getBlock() != null && !clan.getHome().getBlock().getType().equals(Material.AIR)) {
			clan.getHome().getBlock().setType(Material.AIR);
		}
		
		clan.setHome(home);
		this.sql().saveClan(clan);
		
		clan.announceToClan("&s" + ((Player) sender).getName() + " &rset the Clan home to (&s" + x + "&r, &s" + y + "&r, &s" + z + "&r).");
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

	private boolean isInSelfClaim(Player player, Block block) {
		Chunk chunk = block.getChunk();
		ClansPlayer cp = new ClansPlayer(player);
		
		if(!cp.hasClan()) return false;
		if(cp.getClan().getTerritory().size() <= 0) return false;
		
		
		if(cp.getClan().getTerritory().contains(chunk)) return true;
		return false;
	}

}

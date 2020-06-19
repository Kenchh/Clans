package me.rey.clans.commands.base;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.events.clans.ClanTerritoryUnclaimEvent;
import me.rey.clans.events.clans.ClanTerritoryUnclaimEvent.UnclaimReason;
import me.rey.clans.utils.ErrorCheck;
import me.rey.clans.utils.References;
import me.rey.clans.utils.Text;

public class Unclaim extends SubCommand {

	public Unclaim() {
		super("unclaim", "Unclaim a piece of land", ClansRank.ADMIN.getColor() + "/c unclaim <All>", ClansRank.NONE, CommandType.CLAN, true);

	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length > 1) {
			this.sendUsage();
			return;
		}
		
		Player player = (Player) sender;
		Chunk standing = player.getLocation().getChunk();
		ClansPlayer cp = new ClansPlayer(player);
		Clan self = cp.getClan();
		
		// EVENT
		ClanTerritoryUnclaimEvent event = null;
		
		if(args.length == 1 && args[0].equalsIgnoreCase("all")) {
			if(self == null) {
				ErrorCheck.noClan(sender);
				return;
			}
			
			if(!self.getPlayerRank(player.getUniqueId()).equals(ClansRank.LEADER)) {
				ErrorCheck.incorrectRank(sender, ClansRank.LEADER);
				return;
			}
			
			if(self.getTerritory().isEmpty()) {
				this.sendMessageWithPrefix("Error", "You do not have any land claimed!");
				return;
			}
			
			/*
			 * EVENT HANDLING
			 */
			event = new ClanTerritoryUnclaimEvent(self, player, self.getTerritory(), UnclaimReason.NORMAL, true);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			
			self.unclaimAll();
			self.announceToClan("&s" + player.getName() + " &rhas &qUNCLAIMED &rall your land.");
			this.sql().saveClan(self);
			
			return;
		}
		
		if(Main.getInstance().getClanFromTerritory(standing) == null) {
			this.sendMessageWithPrefix("Error", "This territory is not owned by anybody!");
			return;
		}
		
		if(Main.getInstance().getClanFromTerritory(standing) != null && (self == null || !Main.getInstance().getClanFromTerritory(standing).compare(self))) {
			Clan toUnclaim = Main.getInstance().getClanFromTerritory(standing);
			
			int maxChunks = toUnclaim.getPossibleTerritory() <= References.MAX_TERRITORY ? toUnclaim.getPossibleTerritory() : References.MAX_TERRITORY;
			if(!(toUnclaim.getTerritory().size() > maxChunks) || toUnclaim.isServerClan()) {
				this.sendMessageWithPrefix("Error", "You cannot unclaim this land!");
				return;
			}
			
			toUnclaim.removeTerritory(standing);
			this.sql().saveClan(toUnclaim);
			String unclaimer = self == null ? "Player &s" + player.getName() : "Clan &s" + self.getName();
			Text.announceToServer("Clan", unclaimer + " &rhas &4&lUNCLAIMED &ra from &s" + toUnclaim.getName()
			+ "&r. (&s" + standing.getX() + "&r, &s" + standing.getZ() + "&r)");
			
			/*
			 * EVENT HANDLING
			 */
			event = new ClanTerritoryUnclaimEvent(self, player, new ArrayList<Chunk>(Arrays.asList(standing)), UnclaimReason.FORCE, false);
			Bukkit.getServer().getPluginManager().callEvent(event);
			return;
		}
		
		if(self == null) {
			ErrorCheck.noClan(sender);
			return;
		}
		
		if(self.getPlayerRank(player.getUniqueId()).getPower() < ClansRank.ADMIN.getPower() ) {
			ErrorCheck.incorrectRank(sender, ClansRank.ADMIN);
			return;
		}
		
		self.removeTerritory(standing);
		self.announceToClan("&s" + player.getName() + " &rhas &qunclaimed &ra piece of land. (&s" + standing.getX() + "&r, &s" + standing.getZ() + "&r)");
		this.sql().saveClan(self);
		
		/*
		 * EVENT HANDLING
		 */
		event = new ClanTerritoryUnclaimEvent(self, player, new ArrayList<Chunk>(Arrays.asList(standing)), UnclaimReason.NORMAL, false);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}
}

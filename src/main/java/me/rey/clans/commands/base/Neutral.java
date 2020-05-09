package me.rey.clans.commands.base;

import me.rey.clans.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.ErrorCheck;

public class Neutral extends SubCommand {

	public Neutral() {
		super("neutral", "Revoke a relation on a clan", "/c neutral <Clan>", ClansRank.ADMIN, CommandType.CLAN, true);
		this.addAlias("unally");
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length != 1) {
			this.sendUsage();
			return;
		}
		
		ClansPlayer cp = new ClansPlayer((Player) sender);

		Clan toRequest = Main.getInstance().getClan(args[0]);
		Clan from = cp.getClan();
		boolean isPlayerWithClan = this.sql().playerExists(args[0]) && new ClansPlayer(this.sql().getPlayerFromName(args[0]).getUniqueId()).hasClan();
		if(toRequest == null && !isPlayerWithClan) {
			ErrorCheck.clanNotExist(sender);
			return;
		}
		
		Clan to = toRequest == null ? new ClansPlayer(this.sql().getPlayerFromName(args[0]).getUniqueId()).getClan() : toRequest;
		
		if(to.compare(from)) {
			ErrorCheck.actionSelf(sender, "neutral");
			return;
		}
		
		if(to.getClanRelation(from.getUniqueId()) == ClanRelations.NEUTRAL || to.getClanRelation(from.getUniqueId()) == ClanRelations.ENEMY) {
			this.sendMessageWithPrefix("Error", "You are already neutral to that clan!");
			return;
		}
		
		String rName = to.getClanRelation(from.getUniqueId()).getName();
		ChatColor color = to.getClanRelation(from.getUniqueId()).getPlayerColor(); 
		to.removeRelation(from.getUniqueId());
		from.removeRelation(to.getUniqueId());
		this.sql().saveClan(from);
		this.sql().saveClan(to);
		
		from.announceToClan("&sYou &rhave revoked your " + color + rName + " &rstatus with &s" + to.getName() + " &rClan!", cp);
		to.announceToClan("&s" + from.getName() + " &rhas revoked their " + color + rName + " &rstatus with your Clan!", cp);
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

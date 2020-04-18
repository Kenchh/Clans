package me.rey.clans.commands.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.ErrorCheck;

public class Kick extends SubCommand {

	public Kick() {
		super("kick", "Kick a player", "/c kick <Player>", ClansRank.ADMIN, CommandType.CLAN, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length != 1) {
			this.sendUsage();
			return;
		}
		
		ClansPlayer cp = new ClansPlayer((Player) sender);
		if(!cp.hasClan()) {
			ErrorCheck.noClan(sender);
			return;
		}
		
		Clan toKick = cp.getClan();
		if(!toKick.isInClan(args[0])){
			ErrorCheck.specifiedNotInClan(sender);
			return;
		}
		
		ClansPlayer toK = toKick.getPlayer(args[0]);
		String name = toK.isOnline() ? toK.getPlayer().getName() : toK.getOfflinePlayer().getName();
		
		if(toKick.getPlayerRank(toK.getUniqueId()).getPower() >= toKick.getPlayerRank(cp.getUniqueId()).getPower()) {
			ErrorCheck.playerNotOurank(sender);
			return;
		}
		
		boolean success = toKick.kickPlayer(toK.getUniqueId());
		if(!success) {
			this.sendMessageWithPrefix("Error", "You cannot kick this player!");
			return;
		}
		
		toK.save();
		this.sql().saveClan(toKick);
		toK.kick();
		
		String format = String.format("&s%s&r has &qKICKED&r &s%s &rfrom the Clan!", cp.getPlayer().getName(), name);
		toKick.announceToClan(format);
		toK.sendMessageWithPrefix("Clan", "You were kicked from your clan by &s" + cp.getPlayer().getName() + "&r.");
		
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

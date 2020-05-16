package me.rey.clans.commands.base;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.events.clans.ClanHierarchyEvent;
import me.rey.clans.events.clans.ClanHierarchyEvent.HierarchyAction;
import me.rey.clans.events.clans.ClanHierarchyEvent.HierarchyReason;
import me.rey.clans.utils.ErrorCheck;
public class Promote extends SubCommand {

	public Promote() {
		super("promote", "Promote a Player in your clan", "/c promote <Player>", ClansRank.ADMIN, CommandType.CLAN, true);
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
		
		Clan toPromote = cp.getClan();
		if(!toPromote.isInClan(args[0])){
			ErrorCheck.specifiedNotInClan(sender);
			return;
		}
		
		ClansPlayer toProm = toPromote.getPlayer(args[0]);
		String name = toProm.isOnline() ? toProm.getPlayer().getName() : toProm.getOfflinePlayer().getName();
		
		ClansRank origin = toPromote.getPlayerRank(toProm.getUniqueId());
		if(toPromote.getPlayerRank(toProm.getUniqueId()).getPower() >= toPromote.getPlayerRank(cp.getUniqueId()).getPower()) {
			ErrorCheck.playerNotOurank(sender);
			return;
		}
		
		boolean success = toPromote.promote(toProm.getUniqueId());
		if(!success) {
			this.sendMessageWithPrefix("Clan", "This player has the highest rank!");
			return;
		}
		
		ClansRank destination = toPromote.getPlayerRank(toProm.getUniqueId());
		toPromote.announceToClan(String.format("&s%s&r has promoted &s%s &rto %s%s&r!", cp.getPlayer().getName(),
				name, destination.getColor(), destination.getName()));
		
		if(destination == ClansRank.LEADER) {
			toPromote.demote(cp.getUniqueId());
		}
		
		this.sql().saveClan(toPromote);
		
		/*
		 * EVENT HANDLING
		 */
		ClanHierarchyEvent event = new ClanHierarchyEvent(toPromote, cp.getPlayer(), HierarchyAction.PROMOTE, HierarchyReason.NORMAL, toProm, origin, destination);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

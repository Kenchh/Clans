package me.rey.clans.commands.staff;

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

public class SetRank extends SubCommand {

	public SetRank() {
		super("setrank", "Set a player's rank in your clan", "/c x setrank <Player> <Rank>", ClansRank.NONE, CommandType.STAFF, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length != 2) {
			this.sendUsage();
			return;
		}
		
		ClansPlayer cp = new ClansPlayer((Player) sender);
		if(!cp.hasClan()) {
			ErrorCheck.noClan(sender);
			return;
		}
		
		Clan toSet = cp.getClan();
		if(!toSet.isInClan(args[0])){
			ErrorCheck.specifiedNotInClan(sender);
			return;
		}
		
		ClansPlayer toS = toSet.getPlayer(args[0]);
		String name = toS.isOnline() ? toS.getPlayer().getName() : toS.getOfflinePlayer().getName();
		
		ClansRank rank = null, origin = toSet.getPlayerRank(toS.getUniqueId());
		for(ClansRank r : ClansRank.values()) {
			if(r.getName().equalsIgnoreCase(args[1]))
				rank = r;
		}
		
		if(rank == null) {
			ErrorCheck.invalidRank(sender);
			return;
		}
		
		if(cp.isInFakeClan() && cp.getFakeClan().compare(toSet) && toS.compare(cp)) {
			this.sendMessageWithPrefix("Error", "You cannot set your own rank while &smimicking&7.");
			return;
		}
		
		toSet.setRank(toS.getUniqueId(), rank);
		if(this.sql().saveClan(toSet)) {
			toSet.announceToClan(String.format("&s%s&r has made &s%s &ra(n) %s%s&r!", cp.getPlayer().getName(),
					name, rank.getColor(), rank.getName()));
		}
		
		/*
		 * EVENT HANDLING
		 */
		HierarchyAction action = rank.getPower() < origin.getPower() ? HierarchyAction.DEMOTE : HierarchyAction.PROMOTE;
		ClanHierarchyEvent event = new ClanHierarchyEvent(toSet, cp.getPlayer(), action, HierarchyReason.STAFF, toS, origin, rank);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

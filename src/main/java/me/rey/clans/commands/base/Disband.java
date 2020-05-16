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
import me.rey.clans.events.clans.ClanDisbandEvent;
import me.rey.clans.events.clans.ClanDisbandEvent.DisbandReason;

public class Disband extends SubCommand {

	public Disband() {
		super("disband", "Disband your Clan", "/c disband <Clan>", ClansRank.LEADER, CommandType.CLAN, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		ClansPlayer cp = new ClansPlayer((Player) sender);
		cp.getClan().announceToClan("&q" + cp.getPlayer().getName() + " &rdisbanded the Clan!");
		
		Clan self = cp.getClan();
		
		cp.disbandClan();
		
		/*
		 * EVENT HANDLING
		 */
		ClanDisbandEvent event = new ClanDisbandEvent(self, cp.getPlayer(), DisbandReason.NORMAL);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

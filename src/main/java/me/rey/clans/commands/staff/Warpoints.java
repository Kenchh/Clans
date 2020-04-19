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
import me.rey.clans.enums.MathAction;
import me.rey.clans.events.custom.WarpointChangeEvent;
import me.rey.clans.utils.ErrorCheck;
import me.rey.core.utils.Text;

public class Warpoints extends SubCommand {

	public Warpoints() {
		super("warpoints", "Edit your Clan's warpoints on another", "/c x warpoints <add|remove> <Clan> <Value>", ClansRank.NONE, CommandType.STAFF, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length != 3) {
			this.sendUsage();
			return;
		}
		
		MathAction action = null;
		int toAdd = -1;
		for(MathAction tAction : MathAction.values()) {
			if(tAction == MathAction.SET) continue;
			if(tAction.name().equalsIgnoreCase(args[0])) action = tAction;
		}
		
		if(Text.isInteger(args[2]))
			toAdd = Integer.parseInt(args[2]);
		
		if(action == null || toAdd == -1) {
			this.sendUsage();
			return;
		}
		
		if(toAdd < 0) {
			ErrorCheck.invalidNumber(sender);
			return;
		}
		
		if(!this.sql().clanExists(args[1])) {
			ErrorCheck.clanNotExist(sender);
			return;
		}
		
		Clan clan = this.sql().getClan(args[1]), self = new ClansPlayer((Player) sender).getClan();
		long currentWarpoints = self.getWarpointsOnClan(clan.getUniqueId());
		
		long toSet = action.calc(currentWarpoints, toAdd);
		
		self.setWarpoint(clan.getUniqueId(), toSet);
		this.sql().saveClan(self);
		clan.setWarpoint(self.getUniqueId(), -toSet);
		this.sql().saveClan(clan);
		
		clan.announceToClan(String.format("Your War Points on &s%s &rhave been set to: &s%s&r.", self.getName(), -toSet));
		self.announceToClan(String.format("Your War Points on &s%s &rhave been set to: &s%s&r.", clan.getName(), toSet));
		
		if(action == MathAction.REMOVE) {
			WarpointChangeEvent event = new WarpointChangeEvent(clan, self, -toSet);
			Bukkit.getServer().getPluginManager().callEvent(event);
		} else if (action == MathAction.ADD){
			WarpointChangeEvent event = new WarpointChangeEvent(self, clan, toSet);
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}
	
}

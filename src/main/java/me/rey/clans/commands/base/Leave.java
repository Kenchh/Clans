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

public class Leave extends SubCommand {

	public Leave() {
		super("leave", "Leave your Clan", "/c leave", ClansRank.RECRUIT, CommandType.CLAN, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		ClansPlayer cp = new ClansPlayer((Player) sender);
		if(!cp.hasClan()) {
			ErrorCheck.noClan(sender);
			return;
		}
		
		Clan toLeave = cp.getClan();
		if(toLeave.getPlayerRank(cp.getUniqueId()) == ClansRank.LEADER) {
			ErrorCheck.isLeader(sender);
			return;
		}
		
		cp.leaveClan();
		toLeave.announceToClan("&s" + cp.getPlayer().getName() + " &rhas left the Clan!", cp);

	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

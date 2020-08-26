package me.rey.clans.commands.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.events.clans.ClanJoinEvent;
import me.rey.clans.events.clans.ClanJoinEvent.JoinReason;
import me.rey.clans.utils.ErrorCheck;

public class Join extends SubCommand {

	public Join() {
		super("join", "Join a Clan", "/c join <Clan>", ClansRank.NONE, CommandType.CLAN, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length != 1) {
			this.sendUsage();
			return;
		}
		
		ClansPlayer cp = new ClansPlayer((Player) sender);
		if(cp.hasClan()) {
			ErrorCheck.hasClan(sender);
			return;
		}

		HashMap<UUID, ArrayList<UUID>> players = Invite.players;
		Clan toJoin = Main.getInstance().getClan(args[0]);
		if(toJoin == null) {
			ErrorCheck.clanNotExist(sender);
			return;
		}
		
		ArrayList<UUID> invites = players.get(toJoin.getUniqueId());
		if(invites == null || invites.isEmpty() || !invites.contains(cp.getUniqueId())) {
			this.sendMessageWithPrefix("Error", "You have not been invited to join Clan &s" + toJoin.getName() + "&r.");
			return;
		}
		
		Invite.players.get(toJoin.getUniqueId()).remove(cp.getUniqueId());
		
		if(toJoin.hasMaxMembers()) {
			this.sendMessageWithPrefix("Error", "The clan you tried to join is already full!");
			return;
		}
		
		this.sendMessageWithPrefix("Clan", "You have joined Clan &s" + toJoin.getName() + "&r.");
		toJoin.addPlayer(cp.getUniqueId(), ClansRank.RECRUIT);
		this.sql().saveClan(toJoin);
		
		toJoin.announceToClan("&s" + cp.getPlayer().getName() + " &rjoined your Clan!", cp);
		
		/*
		 * EVENT HANDLING
		 */
		ClanJoinEvent event = new ClanJoinEvent(toJoin, cp.getPlayer(), JoinReason.INVITE);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

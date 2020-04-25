package me.rey.clans.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.enums.CommandType;

public class AllyChat extends ClansCommand {

	public static Set<UUID> inChat = new HashSet<>();
	
	public AllyChat() {
		super("ac", "Use your Ally Chat", "/ac (Message)", ClansRank.RECRUIT, CommandType.CLAN, true);
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if(args.length == 0) {
			boolean success = inChat.contains(p.getUniqueId()) ? inChat.remove(p.getUniqueId()) : inChat.add(p.getUniqueId());
			if(success) this.sendMessageWithPrefix("Chat", "Ally Chat: " + (inChat.contains(p.getUniqueId()) ? "&aEnabled" : "&cDisabled"));
			return;
		}
		
		StringBuilder str = new StringBuilder();
		for(String s : args) {
			str.append(s).append(" ");
		}
		
		Clan self = new ClansPlayer(p).getClan();
		ClanRelations r = ClanRelations.ALLY;
		self.shoutToRelation(r, p, str.toString().trim());
	}
	
	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}
	
}


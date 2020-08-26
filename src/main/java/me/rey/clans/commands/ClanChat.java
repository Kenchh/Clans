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
import me.rey.clans.siege.bombs.CustomExplosion;

public class ClanChat extends ClansCommand {
	
	public static Set<UUID> inChat = new HashSet<>();

	public ClanChat() {
		super("cc", "Use your Clan Chat", "/cc (Message)", ClansRank.RECRUIT, CommandType.CLAN, true);
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if(args.length == 0) {
			boolean success = inChat.contains(p.getUniqueId()) ? inChat.remove(p.getUniqueId()) : inChat.add(p.getUniqueId());
			if(success) this.sendMessageWithPrefix("Chat", "Clan Chat: " + (inChat.contains(p.getUniqueId()) ? "&aEnabled" : "&cDisabled"));
			
			return;
		}
		
		StringBuilder str = new StringBuilder();
		for(String s : args) {
			str.append(s).append(" ");
		}
		
		Clan self = new ClansPlayer(p).getClan();
		ClanRelations r = self.getClanRelation(self.getUniqueId());
		
		self.shoutToRelation(r, p, str.toString().trim());
		
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}
	

}

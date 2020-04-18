package me.rey.clans.commands.base;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.clans.ServerClan;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.ErrorCheck;
import me.rey.clans.utils.Text;

public class Create extends SubCommand {
	
	public Create() {
		super("create", "Create a Clan", "/c create <Clan>", ClansRank.NONE, CommandType.CLAN, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		
		if(args.length != 1) {
			this.sendUsage();
			return;
		}
		
		if(new ClansPlayer((Player) sender).hasClan()) {
			ErrorCheck.hasClan(sender);
			return;
		}
		
		String name = args[0];
		if(name.length() < 3) {
			this.sendMessageWithPrefix("Error", "Your Clan name must be at least 3 characters long!");
			return;
		} else if(name.length() > 10) {
			this.sendMessageWithPrefix("Error", "Your Clan name is too long!");
			return;
		}
		
		if(this.sql().clanExists(name)) {
			this.sendMessageWithPrefix("Error", "A clan with that name already exists!");
			return;
		}
		
		for(ServerClan type : ServerClan.values()) {
			if(type.getName().equalsIgnoreCase(args[0])) {
				this.sendMessageWithPrefix("Error", "Invalid clan name!");
				return;
			}
		}
		
		if(!Text.isAlphanumeric(args[0])) {
			this.sendMessageWithPrefix("Error", "Invalid clan name!");
			return;
		}
		
		for(SubCommand sc : source.getChilds()) {
			if(sc.command().equalsIgnoreCase(args[0])) {
				this.sendMessageWithPrefix("Error", "Invalid clan name!");
				return;
			}
		}
		
		this.sql().createClan(UUID.randomUUID(), name, ((Player) sender));
		this.sendMessageWithPrefix("Clan", String.format("You have created Clan &s%s&r.", name));
		
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

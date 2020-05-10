package me.rey.clans.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ewmikey.ranks.User;
import me.rey.clans.Main;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.database.SQLManager;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.ErrorCheck;
import me.rey.clans.utils.References;
import me.rey.clans.utils.Text;
import net.md_5.bungee.api.ChatColor;

public abstract class SubCommand {
	
	private final String command, description;
	private final ClansRank requiredRank;
	private final CommandType commandType;
	private final boolean isPlayerExclusive;
	private final String usage;
	private boolean displayHelp, isForStaff;
	
	CommandSender sender;
	private SQLManager sql = Main.getInstance().getSQLManager();
	
	public SubCommand(String command, String description, String usage, ClansRank requiredRank, CommandType commandType, boolean isPlayerExclusive) {
		this.command = command;
		this.description = description;
		this.requiredRank = requiredRank;
		this.commandType = commandType;
		this.isPlayerExclusive = isPlayerExclusive;
		this.usage = usage;
		this.displayHelp = true;
		this.isForStaff = false;
	}
	
	public void run(ClansCommand source, CommandSender sender, String[] args) {
		if(isPlayerExclusive && !(sender instanceof Player)) return;
		
		if(isStaff() && sender instanceof Player) {
			User user = new User((Player) sender);
			
			if(!user.compareRank(References.getStaffRank())) {
				ErrorCheck.noPermissions(sender, References.getStaffRank());
				return;
			}
		}

		if((sender instanceof Player) && requiredRank != ClansRank.NONE) {
			ClansPlayer cp = new ClansPlayer((Player) sender);
			if(cp.getClan() == null) {
				ErrorCheck.noClan(sender);
				return;
			}
			
			ClansRank rank = cp.getClan().getPlayerRank(cp.getUniqueId());
			if(cp.getClan() == null || rank.getPower() < this.requiredRank().getPower()) {
				ErrorCheck.incorrectRank(sender, requiredRank);
				return;
			}
		}

		this.setSender(sender);
		
		if((this.getChilds() != null && this.getChilds().length != 0) && args.length > 0) {
			
			for(SubCommand argument : this.getChilds()) {
				if(argument.command().equalsIgnoreCase(args[0])) {
					argument.run(source, sender, Arrays.copyOfRange(args, 1, args.length));
					return;
				}
			}
			
		}
		
		this.build(source, sender, args);
	}
	
	public abstract void build(ClansCommand source, CommandSender sender, String[] args);
	public abstract SubCommand[] getChilds();
	
	public String command() {
		return command;
	}

	public String description() {
		return description;
	}

	public ClansRank requiredRank() {
		return requiredRank;
	}

	public CommandType commandType() {
		return commandType;
	}

	public boolean isPlayerExclusive() {
		return isPlayerExclusive;
	}
	
	public void sendUsage() {
		this.sendUsageError(this.usage());
	}
	
	public String usage() {
		return usage;
	}
	
	public SQLManager sql() {
		return sql;
	}
	
	public void setSender(CommandSender sender) {
		this.sender = sender;
	}
	
	public void sendUsageError(String usage) {
		sendMessageWithPrefix("Error", "Incorrect Usage! Usage: &e" + usage);
	}
	
	public void sendMessageWithPrefix(CommandType type, String message) {
		sendMessageWithPrefix(type.getName(), message);
	}
	
	public void sendMessageWithPrefix(String prefix, String message) {
		sendMessage(Text.format(prefix, message));
	}
	
	public void sendMessage(String message) {
		if(sender == null) return;
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public void setDisplayOnHelp(boolean display) {
		this.displayHelp = display;
	}
	
	public boolean displayHelp() {
		return displayHelp;
	}
	
	public boolean isStaff() {
		return isForStaff;
	}
	
	public void setStaff(boolean staff) {
		this.isForStaff = staff;
	}
	
	
}
package me.rey.clans.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

public abstract class ClansCommand implements CommandExecutor {
	
	private final String command, description;
	private final ClansRank requiredRank;
	private final CommandType commandType;
	private final boolean isPlayerExclusive;
	private final String usage;
	private SQLManager sql = Main.getInstance().getSQLManager();
	private CommandSender sender;
	private boolean isForStaff;
	private ArrayList<String> aliases = new ArrayList<String>();
	
	public ClansCommand(String command, String description, String usage, ClansRank requiredRank, CommandType commandType, boolean isPlayerExclusive) {
		this.command = command;
		this.description = description;
		this.requiredRank = requiredRank;
		this.commandType = commandType;
		this.isPlayerExclusive = isPlayerExclusive;
		this.usage = usage;
		this.isForStaff = false;
		
		JavaPlugin.getPlugin(Main.class).getCommand(command).setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase(this.command())) return true;
		if(isPlayerExclusive() && !(sender instanceof Player)) return ErrorCheck.playerOnly(sender);
		
		if(isStaff() && sender instanceof Player) {
			User user = new User((Player) sender);
			
			if(!user.compareRank(References.getStaffRank())) {
				return ErrorCheck.noPermissions(sender, References.getStaffRank());
			}
		}
		
		if((sender instanceof Player) && requiredRank != ClansRank.NONE) {
			ClansPlayer cp = new ClansPlayer((Player) sender);
			if(cp.getClan() == null) {
				return ErrorCheck.noClan(sender);
			}
			
			if(cp.getClan() == null || cp.getClan().getPlayerRank(cp.getUniqueId()).getPower() < this.requiredRank().getPower())
				return ErrorCheck.incorrectRank(sender, requiredRank);
		}
		
		this.setSender(sender);
		
		if((this.getChilds() != null && this.getChilds().length != 0) && args.length > 0) {
			
			for(SubCommand argument : this.getChilds()) {
				if(argument.command().equalsIgnoreCase(args[0]) || argument.hasAlias(args[0])) {
					argument.run(this, sender, Arrays.copyOfRange(args, 1, args.length));
					return true;
				}
			}
			
		}
		
		this.run(sender, args);
		
		return true;
	}
	
	public abstract void run(CommandSender sender, String[] args);
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
		if(sender == null) return;
		sender.sendMessage(Text.format(prefix, message));
	}
	
	public void sendMessage(String message) {
		if(sender == null) return;
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	
	public boolean isStaff() {
		return isForStaff;
	}
	
	public void setStaff(boolean staff) {
		this.isForStaff = staff;
	}
	
	public void addAlias(String alias) {
		if(aliases.contains(alias) == false) {
			aliases.add(alias);
		}
	}

	public boolean hasAlias(String alias) {
		if(aliases.contains(alias)) {
			return true;
		}
		return false;
	}

}

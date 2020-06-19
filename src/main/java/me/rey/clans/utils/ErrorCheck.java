package me.rey.clans.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ewmikey.ranks.UserRank;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;

public class ErrorCheck {

	public static boolean playerOnly(CommandSender sender) {
		sender.sendMessage(Text.color("&cOnly players can use this command!"));
		return true;
	}
	
	public static boolean incorrectRank(CommandSender sender, ClansRank correctRank) {
		sender.sendMessage(Text.format("Clan", String.format("Only %s%ss+&r can do this!", correctRank.getColor(), correctRank.getName())));
		return true;
	}
	
	public static boolean noClan(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "You are not in a clan!"));
		return true;
	}
	
	public static boolean hasClan(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "You are already in a clan!"));
		return true;
	}
	
	public static boolean clanNotExist(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "That clan does not exist!"));
		return true;
	}
	
	public static boolean playerOffline(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "That player is not online!"));
		return true;
	}
	
	public static boolean specifiedNotInClan(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "That player is not in your clan!"));
		return true;
	}
	
	public static boolean playerNotOurank(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "You do not outrank this player!"));
		return true;
	}
	
	public static boolean actionSelf(CommandSender sender, String action) {
		sender.sendMessage(Text.format("Error", "You cannot " + action + " yourself!"));
		return true;
	}

	public static boolean isLeader(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "You are the leader!"));
		return true;
	}
	
	public static boolean invalidRank(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "That rank is invalid!"));
		return true;
	}
	
	public static boolean noPermissions(CommandSender sender, UserRank rank) {
		sender.sendMessage(Text.format("Permission", "You do not have permission to run this command."));
		return true;
	}
	
	public static boolean noPermissionInClaim(Player player, Clan claimed) {
		ChatColor color = new ClansPlayer(player).hasClan() ?
				claimed.getClanRelation(new ClansPlayer(player).getClan().getUniqueId()).getPlayerColor() : ClanRelations.NEUTRAL.getPlayerColor();
		player.sendMessage(Text.format("Error", "You cannot do that while in territory of " + color + claimed.getName() + "&r."));
		return true;
	}
	
	public static boolean noPermissionNearClaim(Player player, Clan claimed) {
		ChatColor color = new ClansPlayer(player).hasClan() ?
				claimed.getClanRelation(new ClansPlayer(player).getClan().getUniqueId()).getPlayerColor() : ClanRelations.NEUTRAL.getPlayerColor();
		player.sendMessage(Text.format("Error", "You cannot build so close to " + color + claimed.getName() + "&r."));
		return true;
	}
	
	public static boolean invalidNumber(CommandSender sender) {
		sender.sendMessage(Text.format("Error", "That number is invalid!"));
		return true;
	}
	
}

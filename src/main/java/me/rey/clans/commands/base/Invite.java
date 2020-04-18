package me.rey.clans.commands.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.ErrorCheck;

public class Invite extends SubCommand {

	public static HashMap<UUID, ArrayList<UUID>> players = new HashMap<>();
	private final int inviteExpiresSeconds = 60;
	
	public Invite() {
		super("invite", "Invite a Player to your Clan", "/c invite <Player>", ClansRank.ADMIN, CommandType.CLAN, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length != 1) {
			this.sendUsage();
			return;
		}
		
		ClansPlayer cp = new ClansPlayer((Player) sender);
		Player playerToInvite = Bukkit.getServer().getPlayer(args[0]);
		if(playerToInvite == null || !playerToInvite.isOnline()) {
			ErrorCheck.playerOffline(sender);
			return;
		}
		
		if(playerToInvite.getUniqueId() == ((Player) sender).getUniqueId()) {
			ErrorCheck.actionSelf(sender, "invite");
			return;
		}
		
		Clan toInvite = new ClansPlayer((Player) sender).getClan();
		if(toInvite.hasMaxMembers()) {
			this.sendMessageWithPrefix("Error", "Your clan is already full!");
			return;
		}
		
		ArrayList<UUID> currentInvites = players.get(toInvite.getUniqueId());
		if(currentInvites != null && currentInvites.contains(playerToInvite.getUniqueId())) {
			this.sendMessageWithPrefix("Error", "That player has already been invited!");
			return;
		}
		
		new ClansPlayer(playerToInvite).sendMessageWithPrefix(
				"Clan", "&s" + cp.getPlayer().getName() + " &rhas invited you to join Clan &s" + toInvite.getName() + "&r.");
		ArrayList<UUID> cache = players.get(toInvite.getUniqueId()) == null ? new ArrayList<UUID>() : players.get(toInvite.getUniqueId());
		cache.add(playerToInvite.getUniqueId());
		players.put(toInvite.getUniqueId(), cache);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				ArrayList<UUID> newCurrent = players.get(toInvite.getUniqueId()) ;
				if(newCurrent != null && !newCurrent.isEmpty() && newCurrent.contains(playerToInvite.getUniqueId())) {
					players.get(toInvite.getUniqueId()).remove(playerToInvite.getUniqueId());
					this.cancel();
					return;
				}
			}
			
		}.runTaskLater(JavaPlugin.getPlugin(Main.class), inviteExpiresSeconds * 20);
		
		toInvite.announceToClan("&s" + cp.getPlayer().getName() + " &rhas invited &s" + playerToInvite.getName() + " &rto your Clan!");
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}
}

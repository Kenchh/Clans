package me.rey.clans.commands.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.ErrorCheck;

public class Ally extends SubCommand {

	private static HashMap<UUID, ArrayList<UUID>> requests = new HashMap<>();
	private final int inviteExpiresSeconds = 60;
	
	public Ally() {
		super("ally", "Request an Alliance to a Clan", "/c ally <Clan>", ClansRank.ADMIN, CommandType.CLAN, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length != 1) {
			this.sendUsage();
			return;
		}
		
		ClansPlayer cp = new ClansPlayer((Player) sender);

		Clan toRequest = this.sql().getClan(args[0]);
		Clan from = cp.getClan();
		boolean isPlayerWithClan = this.sql().playerExists(args[0]) && new ClansPlayer(this.sql().getPlayerFromName(args[0]).getUniqueId()).hasClan();
		if(toRequest == null && !isPlayerWithClan) {
			ErrorCheck.clanNotExist(sender);
			return;
		}
		
		Clan to = toRequest == null ? new ClansPlayer(this.sql().getPlayerFromName(args[0]).getUniqueId()).getClan() : toRequest;
		
		if(to.compare(from)) {
			ErrorCheck.actionSelf(sender, "ally");
			return;
		}
		
		if(to.getClanRelation(from.getUniqueId()) != ClanRelations.NEUTRAL && to.getClanRelation(from.getUniqueId()) != ClanRelations.ENEMY) {
			ClanRelations relation = to.getClanRelation(from.getUniqueId());
			String rName = relation.getName().endsWith("y") ? relation.getName().substring(0, relation.getName().length()-1) + "ied" :
				relation.getName() + "d";
			this.sendMessageWithPrefix("Error", "You are already " + relation.getPlayerColor() + rName + " &rto that clan!");
			return;
		}
		
		if(from.hasMaxAllies() || to.hasMaxAllies()) {
			this.sendMessageWithPrefix("Error", (to.hasMaxAllies() ? "Clan &s" + to.getName() + "&r" : "Your clan") + " already has max allies!");
			return;
		}
		
		ArrayList<UUID> requestsFromClan = requests.get(to.getUniqueId()) == null ? new ArrayList<>() : requests.get(to.getUniqueId());
		ArrayList<UUID> self = requests.get(from.getUniqueId()) == null ? new ArrayList<>() : requests.get(from.getUniqueId());
		if(requestsFromClan.isEmpty() || !requestsFromClan.contains(from.getUniqueId()) || self.contains(to.getUniqueId())) {
			
			if(self.contains(to.getUniqueId())) {
				this.sendMessageWithPrefix("Error", "Please wait before doing that again!");
				return;
			}
			
			from.announceToClan("&s" + cp.getPlayer().getName() + " &rhas sent an " 
					+ ClanRelations.ALLY.getPlayerColor() + "alliance&r request to &s" + to.getName() + "&r.");
			
			to.announceToClan("&s" + from.getName() + " &rhas requested to" + ClanRelations.ALLY.getPlayerColor() + " ally &ryou!");
			
			self.add(to.getUniqueId());
			requests.put(from.getUniqueId(), self);
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					ArrayList<UUID> selfNew = requests.get(from.getUniqueId());
					if(selfNew != null && !selfNew.isEmpty() && selfNew.contains(to.getUniqueId())) {
						requests.get(from.getUniqueId()).remove(to.getUniqueId());
						this.cancel();
						return;
					}
				}
				
			}.runTaskLater(JavaPlugin.getPlugin(Main.class), inviteExpiresSeconds * 20);
			
			return;
		}
	
		to.setRelation(from.getUniqueId(), ClanRelations.ALLY);
		from.setRelation(to.getUniqueId(), ClanRelations.ALLY);
		requests.get(to.getUniqueId()).remove(from.getUniqueId());
		this.sql().saveClan(from);
		this.sql().saveClan(to);
		
		from.announceToClan("&s" + to.getName() + " &rhas " + ClanRelations.ALLY.getPlayerColor() + "&rallied you!", cp);
		to.announceToClan("&s" + from.getName() + " &rhas " + ClanRelations.ALLY.getPlayerColor() + "&rallied you!", cp);
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}
	

}

package me.rey.clans.commands.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.ErrorCheck;

public class Set extends SubCommand {

	public Set() {
		super("set", "Set or reset your dummy Clan", "/c x set <Clan>", ClansRank.NONE, CommandType.STAFF, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length > 1) {
			this.sendUsage();
			return;
		}
		
		ClansPlayer staff = new ClansPlayer((Player) sender);
		if(args.length == 0) {
			boolean isInDummyClan = staff.isInFakeClan();
			if(isInDummyClan) {
				this.sendMessageWithPrefix("Staff", "You have reset your &smimic &rclan.");
				Main.adminFakeClans.remove(staff.getUniqueId());
			} else {
				this.sendMessageWithPrefix("Error", "You must set a &smimic &rclan first.");
			}
			return;
		}
		
		Clan toJoin = Main.getInstance().getClan(args[0]);
		if(toJoin == null) {
			ErrorCheck.clanNotExist(sender);
			return;
		}
		
		if(staff.getRealClan() != null && toJoin.compare(staff.getRealClan())) {
			ErrorCheck.actionSelf(sender, "mimic");
			return;
		}
		
		if(staff.isInFakeClan() && staff.getFakeClan().compare(toJoin)) {
			this.sendMessageWithPrefix("Error", "You are already &smimicking &rthis clan!");
			return;
		}
		
		Main.adminFakeClans.put(staff.getUniqueId(), toJoin.getUniqueId());
		this.sendMessageWithPrefix("Staff", "Your mimic clan is now &s" + staff.getClan().getName() + "&r.");
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

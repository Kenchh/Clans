package me.rey.clans.commands.staff;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.enums.MathAction;
import me.rey.clans.utils.ErrorCheck;
import me.rey.core.utils.Text;

public class Energy extends SubCommand {

	public Energy() {
			super("energy", "Edit a Clan's energy value", "/c x energy <set|add|remove> <Clan> <Value>", ClansRank.NONE, CommandType.STAFF, true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		if(args.length != 3) {
			this.sendUsage();
			return;
		}
		
		MathAction action = null;
		int energy = -1;
		for(MathAction tAction : MathAction.values()) {
			if(tAction.name().equalsIgnoreCase(args[0])) action = tAction;
		}
		
		if(Text.isInteger(args[2]))
			energy = Integer.parseInt(args[2]);
		
		if(action == null || energy == -1) {
			this.sendUsage();
			return;
		}
		
		if(energy < 0) {
			ErrorCheck.invalidNumber(sender);
			return;
		}
		
		if(!this.sql().clanExists(args[1])) {
			ErrorCheck.clanNotExist(sender);
			return;
		}
		
		Clan clan = this.sql().getClan(args[1]);
		int toSet = action.calc((int) clan.getEnergy(), energy) <= 0 ? 0 : action.calc((int) clan.getEnergy(), energy);
		
		clan.setEnergy(toSet);
		this.sql().saveClan(clan);
		
		clan.announceToClan("Your Energy has been set to: &s" + toSet + "&r.");
		new ClansPlayer((Player) sender).sendMessageWithPrefix("Staff", "You have set energy of &s" + clan.getName() + " &rto: &s" + toSet + "&r.");
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}
	
}

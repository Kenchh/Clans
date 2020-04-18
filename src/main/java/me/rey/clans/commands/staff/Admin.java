package me.rey.clans.commands.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.Text;

public class Admin extends SubCommand {

	public Admin() {
		super("x", "Base staff command", "/c x <command>", ClansRank.NONE, CommandType.STAFF, true);
		this.setDisplayOnHelp(false);
		this.setStaff(true);
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {		
		sender.sendMessage(Text.format(this.commandType().getName(), "Admin Commands List:"));
		
		for(SubCommand subCommand : this.getChilds()) {
			if(!subCommand.displayHelp()) continue;
			ChatColor c = subCommand.requiredRank().getColor();
			sender.sendMessage(c + Text.color(String.format("%s &7%s",
					subCommand.usage(),
					subCommand.description()
					)));
		}
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {
				new ForceJoin(),
				new SetRank(),
				new Set(),
				new Energy()
		};
	}

}

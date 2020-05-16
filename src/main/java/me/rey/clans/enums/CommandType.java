package me.rey.clans.enums;

import me.rey.clans.utils.Text;

public enum CommandType {
	
	HELP(),
	STAFF(),
	FEATURE(),
	CLAN();
	
	public String getName() {
		return Text.formatName(this.name());
	}
	
}

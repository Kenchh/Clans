package me.rey.clans.packets;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.rey.clans.utils.NMSUtil;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;

public class Nametag extends Packets {

	private Player tag;
	private String text, team;
	
	public Nametag(Player tag, String team, String text) {
		this.tag = tag;
		this.text = text;
		this.team = team;
		
	}

	@Override
	public void send(LivingEntity entity) {
		
        net.minecraft.server.v1_8_R3.Scoreboard scoreboard = ((CraftScoreboardManager)Bukkit.getServer().getScoreboardManager()).getNewScoreboard().getHandle();
        ScoreboardTeam team = scoreboard.getTeam(this.team);
        if (team == null) team = scoreboard.createTeam(this.team);

        PacketPlayOutScoreboardTeam removePacket = new PacketPlayOutScoreboardTeam(team, 1);
        PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam(team, 0);
        PacketPlayOutScoreboardTeam teamColorPacket = new PacketPlayOutScoreboardTeam(team, 2);
        PacketPlayOutScoreboardTeam joinPacket = new PacketPlayOutScoreboardTeam(team, Arrays.asList(tag.getName()), 3);

        if(this.text.length() > 15) {
        	System.out.println("==========================");
        	System.out.println("[TAB PACKET] String length too long: " + this.text);
        	System.out.println("==========================");
        	return;
        }
        
        /* PREFIX */ NMSUtil.getAndSetField(teamColorPacket.getClass(), "c", teamColorPacket, this.text);
        /* SUFFIX */ NMSUtil.getAndSetField(teamColorPacket.getClass(), "d", teamColorPacket, "");
        
        this.sendPacket((Player) entity, removePacket);
        this.sendPacket((Player) entity, teamPacket);
        this.sendPacket((Player) entity, teamColorPacket);
        this.sendPacket((Player) entity, joinPacket);
	}

}

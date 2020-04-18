package me.rey.clans.commands.base;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.SubCommand;
import me.rey.clans.enums.CommandType;
import me.rey.clans.packets.Title;
import me.rey.clans.utils.Text;

public class Home extends SubCommand implements Listener {

	private static ArrayList<UUID> teleporting = new ArrayList<>();
	private final double homeTimer = 15.00;
	
	public Home() {
		super("home", "Teleport to Clan home", "/c home", ClansRank.RECRUIT, CommandType.CLAN, true);
		Bukkit.getServer().getPluginManager().registerEvents(this, JavaPlugin.getPlugin(Main.class));
	}

	@Override
	public void build(ClansCommand source, CommandSender sender, String[] args) {
		ClansPlayer cp = new ClansPlayer((Player) sender);
		Clan self = cp.getClan();
		
		if(self.getHome() == null) {
			cp.sendMessageWithPrefix("Clan", "Your clan home is not set!");
			return;
		}
		
		if(cp.isInCombat()) {
			cp.sendMessageWithPrefix("Error", "You cannot teleport while in combat!");
			return;
		}
		
		if(teleporting.contains(cp.getUniqueId())) {
			cp.sendMessageWithPrefix("Error", "You are already teleporting!");
			return;
		}
		
		Location home = self.getHome();
		home.setX(home.getBlockX() + 0.5);
		home.setY(home.getBlockY() + 1);
		home.setZ(home.getBlockZ() + 0.5);
		
		UUID uuid = cp.getUniqueId();
		teleporting.add(uuid);
		
		double decrement = 0.1;
		new BukkitRunnable() {
			
			double timer = homeTimer;
			
			@Override
			public void run() {
				if(!teleporting.contains(uuid)){
					new Title("", Text.color("&cTeleport cancelled."), 0, (int) (decrement * 20) + 1, 20).send((Player) sender);
					this.cancel();
					return;
				}
				
				if(timer <= 0) {
					((Player) sender).teleport(home);
					cp.sendMessageWithPrefix("Clan", "You have teleported to your Clan home.");
					teleporting.remove(uuid);
					this.cancel();
					return;
				}
				
				String format = String.format("%.1f", timer);
				new Title("", Text.color("Teleporting in &a" + format + " &fseconds."), 0, (int) (decrement * 20) + 1, 0).send((Player) sender);
				timer = timer - decrement;
			}
			
		}.runTaskTimer(JavaPlugin.getPlugin(Main.class), 0, (int) (decrement * 20));
	}
	
	@EventHandler
	public void onHomeCancel(PlayerMoveEvent e) {
		if(!teleporting.contains(e.getPlayer().getUniqueId())) return;
		if(e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY() && e.getTo().getBlockZ() == e.getFrom().getBlockZ()) return;
		
		teleporting.remove(e.getPlayer().getUniqueId());
	}

	@Override
	public SubCommand[] getChilds() {
		return new SubCommand[] {};
	}

}

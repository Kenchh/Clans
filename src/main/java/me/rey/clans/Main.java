package me.rey.clans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.rey.clans.clans.Clan;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.base.Base;
import me.rey.clans.database.SQLManager;
import me.rey.clans.events.ClaimProtection;
import me.rey.clans.events.CombatBaseRelation;
import me.rey.clans.events.PlayerChat;
import me.rey.clans.events.PlayerDeath;
import me.rey.clans.events.PlayerJoin;
import me.rey.clans.events.Shops;
import me.rey.clans.events.TerritoryChange;
import me.rey.clans.features.EnergyHandler;
import me.rey.clans.features.ServerTerritoriesParse;
import me.rey.clans.packets.PlayerInfo;
import me.rey.clans.utils.Text;

public class Main extends JavaPlugin {
	
	Plugin plugin;
	PluginManager pm = Bukkit.getPluginManager();
	ArrayList<ClansCommand> commands;
	
	private SQLManager sql;
	private static Main instance;
	public static HashMap<UUID, UUID> adminFakeClans;
	public static HashMap<Chunk, UUID> territory;
	public static Set<String> safeZoneCoords;
	public static ArrayList<UUID> clans;
	private ServerTerritoriesParse stp;
	
	/*
	 * Called on plugin enable
	 */
	public void onEnable() {
		this.plugin = this;
		instance = this;
		adminFakeClans = new HashMap<>();
		
		stp = new ServerTerritoriesParse();
		stp.init();
		this.pm.registerEvents(stp, this);
		
		
		loadConfig();
		initDatabase();
		clans = this.getSQLManager().getClans();
		safeZoneCoords = this.getSQLManager().getSafeZones();
		
		this.registerCommands();
		this.registerListeners();
		
		/*
		 * ENERGY HANDLER
		 */
		EnergyHandler energyHandler = new EnergyHandler(this.getSQLManager());
		energyHandler.start();
		
		/*
		 * SERVER CLANS
		 */
		this.getSQLManager().loadServerClans();
		
		/*
		 * SCOREBOARD
		 */
		PlayerInfo info = new PlayerInfo();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Player online : Bukkit.getOnlinePlayers()) {
					info.updateScoreboard(online);
				}
			}
			
		}.runTaskTimer(this, 0, 4);
	}
	
	
	
	/*
	 * Called on plugin disable
	 */
	public void onDisable() {
		
		this.plugin = null;
	}
	
	
	private SQLManager initDatabase() {
		sql = new SQLManager(this);
		
		Text.log(this, "=====================================");
		Text.log(this, "");
		Text.log(this, "&a&lMySQL database connected!");
		Text.log(this, "&f&lClans now has access to all player data");
		Text.log(this, "");
		Text.log(this, "=====================================");
		
		territory = sql.loadTerritories();
		
		return sql;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public SQLManager getSQLManager() {
		return sql;
	}
	
	/*
	 * Command registration
	 * getCommand("command").setExector(new Class());
	 */
	public void registerCommands() {
		this.commands = new ArrayList<ClansCommand>(Arrays.asList(
				new Base()
				));
	}
	
	
	
	/*
	 * Listener registration
	 * PluginManager#registerEvents(new Class(), this);
	 */
	
	public void registerListeners() {
		pm.registerEvents(new PlayerChat(), this);
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerDeath(), this);
		pm.registerEvents(new TerritoryChange(), this);
		pm.registerEvents(new ClaimProtection(), this);
		pm.registerEvents(new CombatBaseRelation(), this);
		pm.registerEvents(new Shops(), this);
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public World getClansWorld() {
		return Bukkit.getServer().getWorld(this.getConfig().getString("clans-world"));
	}
	
	public Clan getClanFromTerritory(Chunk chunk) {
		if(!territory.containsKey(chunk))
			return null;
		if(!this.getSQLManager().clanExists(territory.get(chunk))) {
			territory.remove(chunk);
			return null;
		}
		
		return this.getSQLManager().getClan(territory.get(chunk));
	}

}

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
import me.rey.clans.clans.WarriorsTeamHandler;
import me.rey.clans.commands.AllyChat;
import me.rey.clans.commands.ClanChat;
import me.rey.clans.commands.ClansCommand;
import me.rey.clans.commands.Focus;
import me.rey.clans.commands.base.Base;
import me.rey.clans.database.SQLManager;
import me.rey.clans.events.ClaimProtection;
import me.rey.clans.events.CombatBaseRelation;
import me.rey.clans.events.PlayerChat;
import me.rey.clans.events.PlayerDeath;
import me.rey.clans.events.PlayerJoin;
import me.rey.clans.events.TerritoryChange;
import me.rey.clans.features.EnergyHandler;
import me.rey.clans.features.ServerParser;
import me.rey.clans.items.crafting.BoosterAxe;
import me.rey.clans.items.crafting.BoosterSword;
import me.rey.clans.items.crafting.IronDoor1;
import me.rey.clans.items.crafting.IronTrapDoor1;
import me.rey.clans.items.crafting.PowerAxe;
import me.rey.clans.items.crafting.PowerSword;
import me.rey.clans.items.crafting.marksman.MBoots1;
import me.rey.clans.items.crafting.marksman.MBoots2;
import me.rey.clans.items.crafting.marksman.MChestplate1;
import me.rey.clans.items.crafting.marksman.MChestplate2;
import me.rey.clans.items.crafting.marksman.MHelmet1;
import me.rey.clans.items.crafting.marksman.MHelmet2;
import me.rey.clans.items.crafting.marksman.MLeggings1;
import me.rey.clans.items.crafting.marksman.MLeggings2;
import me.rey.clans.playerdisplay.PlayerInfo;
import me.rey.clans.shops.Shops;
import me.rey.clans.siege.SiegeTriggerEvent;
import me.rey.clans.utils.Text;
import me.rey.clans.utils.UtilFocus;
import me.rey.clans.worldevents.ClansEvents;

public class Main extends JavaPlugin {
	
	Plugin plugin;
	PluginManager pm = Bukkit.getPluginManager();
	ArrayList<ClansCommand> commands;
	
	private SQLManager sql;
	private static Main instance;
	public static HashMap<UUID, UUID> adminFakeClans;
	public static Set<String> safeZoneCoords;
	public static ArrayList<Clan> clans;
	public HashMap<Chunk, UUID> territory;
	private ServerParser stp;
	public static HashMap<UUID, HashMap<String, Object>> playerdata;
	
	private PlayerInfo info;

	/*
	 * Called on plugin enable
	 */
	public void onEnable() {
		this.plugin = this;
		instance = this;
		adminFakeClans = new HashMap<>();
		
		loadConfig();
		initDatabase();
		
		stp = new ServerParser();
		stp.init();
		this.pm.registerEvents(stp, this);

		safeZoneCoords = this.getSQLManager().getSafeZones();
		territory = new HashMap<Chunk, UUID>();
		territory.putAll(this.getSQLManager().loadTerritories());
		clans = this.getSQLManager().getClans();
		playerdata = this.getSQLManager().getAllPlayerData();

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
		 * RECIPES
		 */
		new MHelmet1().init().register();
		new MHelmet2().init().register();
		new MChestplate1().init().register();
		new MChestplate2().init().register();
		new MLeggings1().init().register();
		new MLeggings2().init().register();
		new MBoots1().init().register();
		new MBoots2().init().register();
		
		new PowerSword().init().register();
		new PowerAxe().init().register();
		new BoosterSword().init().register();
		new BoosterAxe().init().register();
		
		new IronDoor1().init().register();
		new IronTrapDoor1().init().register();
		
		
		/*
		 * SCOREBOARD
		 */
		info = new PlayerInfo();
		pm.registerEvents(info, this);
		for(Player online : Bukkit.getOnlinePlayers()) {
			info.setupSidebar(online);
		}
		
		/*
		 * NAMETAGS
		 */
		new BukkitRunnable() {
			@Override
			public void run() {
				info.updateNameTagsForAll();
				info.updateTabListForAll();
			}
		}.runTaskTimerAsynchronously(this, 0, 5);
		
		pm.registerEvents(new WarriorsTeamHandler(), this);
		
		/*
		 * WORLD EVENTS
		 */
		ClansEvents worldEvents = new ClansEvents();
		worldEvents.register();
		pm.registerEvents(worldEvents, this);
	}
	
	
	
	/*
	 * Called on plugin disable
	 */
	public void onDisable() {
		
		this.plugin = null;
		
		sql.onDisable();
	}
	
	
	private SQLManager initDatabase() {
		sql = new SQLManager(this);
		
		Text.log(this, "=====================================");
		Text.log(this, "");
		Text.log(this, "&a&lMySQL database connected!");
		Text.log(this, "&f&lClans now has access to all player data");
		Text.log(this, "");
		Text.log(this, "=====================================");
		
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
				new Base(),
				new ClanChat(),
				new AllyChat(),
				new Focus()
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
		pm.registerEvents(new SiegeTriggerEvent(), this);
		pm.registerEvents(new UtilFocus(), this);
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public World getClansWorld() {
		return Bukkit.getServer().getWorld(this.getConfig().getString("clans-world"));
	}
	
	public Clan getClanFromTerritory(Chunk c) {
		
		for(Chunk chunk : this.territory.keySet()) {
			if(c.getX() == chunk.getX() && c.getZ() == chunk.getZ()) {
				
				UUID uuid = this.territory.get(chunk);
				if(!this.getSQLManager().clanExists(uuid)) {
					this.territory.remove(chunk);
					return null;
				}
				
				Clan toGive = this.getClan(uuid);
				return toGive;
			}
		}
		
		return null;
	} 
	
	@SuppressWarnings("unchecked")
	public boolean removeTerritory(Chunk c) {
		if(this.getClanFromTerritory(c) == null) return false;
		
		for(Chunk chunk : ((HashMap<Chunk, UUID>) territory.clone()).keySet()) {
			if(c.getX() == chunk.getX() && c.getZ() == chunk.getZ()) {
				this.territory.remove(chunk);
				return true;
			}
		}
		
		return false;
	}

	public Clan getClan(UUID uuid) {
		for(Clan c : clans) {
			if(c.getUniqueId().equals(uuid)) {
				return c;
			}
		}
		return null;
	}

	public Clan getClan(String name) {
		for(Clan c : clans) {
			if(c.getName().equalsIgnoreCase(name)) {
				return c;
			}
		}
		return null;
	}

}

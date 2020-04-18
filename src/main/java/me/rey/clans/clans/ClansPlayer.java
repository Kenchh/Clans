package me.rey.clans.clans;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.rey.clans.Main;
import me.rey.clans.database.SQLManager;
import me.rey.clans.enums.CommandType;
import me.rey.clans.utils.Text;
import me.rey.core.Warriors;
import me.rey.core.players.PlayerHitCache;

public class ClansPlayer {
	
	private final UUID uuid;
	private Player player;
	private OfflinePlayer offline;
	private Main plugin;
	private SQLManager sql;
	
	public ClansPlayer(Player p) {
		this.uuid = p.getUniqueId();
		this.player = p;
		this.offline = null;
		this.plugin = Main.getInstance();
		
		this.sql = plugin.getSQLManager();
	}
	
	public ClansPlayer(UUID uuid) {
		this.uuid = uuid;
		this.player = Bukkit.getServer().getPlayer(uuid);
		this.offline = null;
		
		if(player == null || !player.isOnline()) {
			player = null;
			this.offline = Bukkit.getServer().getOfflinePlayer(uuid);
		}
		this.plugin = Main.getInstance();
		
		this.sql = plugin.getSQLManager();
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public boolean compare(ClansPlayer player) {
		return this.getUniqueId().equals(player.getUniqueId());
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return offline;
	}
	
	public boolean isOnline() {
		return player != null && player.isOnline();
	}
	
	public boolean hasClan() {
		return this.isInFakeClan() || sql.hasClan(uuid);
	}
	
	public Clan getClan() {
		return this.isInFakeClan() ? this.getFakeClan() : this.getRealClan();
	}
	
	public boolean leaveClan() {
		if(!this.hasClan()) return false;
		
		Clan toSave = this.getClan();
		this.sendMessageWithPrefix("Clan", "You have left &s" + toSave.getName() + "&r.");
		if(isInFakeClan()) {
			Main.adminFakeClans.remove(this.getUniqueId());
			return true;
		}
		
		if(toSave.getPlayerRank(this.getUniqueId()) == ClansRank.LEADER) return false;
		
		toSave.kickPlayer(this.getUniqueId());
		this.sql.saveClan(toSave);
		this.save();
		return true;
	}
	
	public void kick() {
		this.sql.setPlayerData(this.getUniqueId(), "clan", null);
	}
	
	public boolean disbandClan() {
		if(!this.hasClan()) return false;
		this.sql.deleteClan(this.getClan().getUniqueId());
		this.sendMessageWithPrefix("Success", "You have disbanded your Clan.");
		return true;
	}
	
	public void sendMessageWithPrefix(CommandType type, String message) {
		sendMessageWithPrefix(type.getName(), message);
	}
	
	public void sendMessageWithPrefix(String prefix, String message) {
		sendMessage(Text.format(prefix, message));
	}
	
	public void sendMessage(String message) {
		if(player == null) return;
		player.sendMessage(Text.color("&7" + message));
	}
	
	public void save() {
		this.sql.savePlayer(this.getUniqueId());
	}
	
	public Clan getRealClan() {
		String uuid = (String) this.sql.getPlayerData(this.getUniqueId(), "clan");
		if(uuid == null) return null;
		Clan toGive = this.sql.getClan(UUID.fromString(uuid)); 
		return toGive;
	}
	
	public Clan getFakeClan() {
		return !isInFakeClan() ? null : this.sql.getClan(Main.adminFakeClans.get(this.getUniqueId()));
	}
	
	public boolean isInFakeClan() {
		return Main.adminFakeClans.containsKey(this.getUniqueId());
	}
	
	public int getGold() {
		this.sql.createPlayer(this.getUniqueId());
		return (int) this.sql.getPlayerData(this.getUniqueId(), "gold");
	}
	
	public Clan getClanInTerritory() {
		Chunk chunk = this.getPlayer().getLocation().getChunk();
		return Main.territory.containsKey(chunk) ? this.sql.getClan(Main.territory.get(chunk)) : null;
	}
	
	public boolean isInSafeZone() {
		Location loc = this.getPlayer().getLocation();
		String self = String.format("%s;%s;%s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		return Main.safeZoneCoords.contains(self);
	}
	
	public boolean isInCombat() {
		PlayerHitCache cache = Warriors.getInstance().getHitCache();
		return cache.hasCombatTimer(this.getPlayer());
	}

}
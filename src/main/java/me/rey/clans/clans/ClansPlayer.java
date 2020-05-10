package me.rey.clans.clans;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.rey.clans.Main;
import me.rey.clans.database.SQLManager;
import me.rey.clans.enums.CommandType;
import me.rey.clans.siege.Siege;
import me.rey.clans.utils.Text;
import me.rey.clans.utils.UtilFocus;
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
		
		if(this.getClan().isBeingSieged()) {
			ArrayList<Siege> raidingSelf = new ArrayList<>();
			
			for(Siege siege : this.getClan().getClansSiegingSelf()) {
				raidingSelf.add(siege);
			}
			
			for(Siege siege : raidingSelf) {
				siege.end();
			}
		}
		
		if(this.getClan().isSiegingOther()) {
			ArrayList<Siege> siegingOther = new ArrayList<>();
			
			for(Siege siege : this.getClan().getClansSiegedBySelf()) {
				siegingOther.add(siege);
			}
			
			for(Siege siege : siegingOther) {
				siege.end();
			}
		}
		
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
		String uuid = null;
		try {
			uuid = (String) Main.playerdata.get(this.getUniqueId()).get("clan");
		} catch (NullPointerException e) {
			return null;
		}
		if(uuid == null) return null;
		Clan toGive = Main.getInstance().getClan(UUID.fromString(uuid));
		return toGive;
	}
	
	public Clan getFakeClan() {
		return !isInFakeClan() ? null : Main.getInstance().getClan(Main.adminFakeClans.get(this.getUniqueId()));
	}
	
	public boolean isInFakeClan() {
		return Main.adminFakeClans.containsKey(this.getUniqueId());
	}
	
	public int getGold() {
		this.sql.createPlayer(this.getUniqueId());
		return (int) Main.playerdata.get(this.getUniqueId()).get("gold");
	}
	
	public void setGold(int gold) {
		this.sql.setPlayerData(this.getUniqueId(), "gold", gold);
	}
	
	public Clan getClanInTerritory() {
		return Main.getInstance().getClanFromTerritory(this.getPlayer().getLocation().getChunk());
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
	
	public void unfocus() {
		UtilFocus.focus(this.getPlayer(), null);
	}
	
	public void focus(Player focus) {
		UtilFocus.focus(this.getPlayer(), focus);
	}
	
	public boolean hasFocus() {
		return this.getFocus() != null;
	}
	
	public Player getFocus() {
		return UtilFocus.getFocus(this.getPlayer());
	}

}

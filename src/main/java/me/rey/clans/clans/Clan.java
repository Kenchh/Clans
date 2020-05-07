package me.rey.clans.clans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.rey.clans.Main;
import me.rey.clans.siege.Siege;
import me.rey.clans.utils.References;
import me.rey.core.utils.Text;;

public class Clan {
	
	String name;
	String founder;
	private final UUID uuid;
	HashMap<UUID, ClansRank> players;
	ArrayList<Chunk> territory;
	HashMap<UUID, Long> warpoints;
	HashMap<UUID, ClanRelations> clanRelations;
	Location home;
	private long energy = 0;
	
	public Clan(String name, String founder, UUID uuid, ClansPlayer[] players) {
		this.name = name;
		this.founder = founder;
		this.uuid = uuid;
		this.warpoints = new HashMap<UUID, Long>();
		this.clanRelations = new HashMap<UUID, ClanRelations>();
		this.home = null;
		
		this.players = new HashMap<UUID, ClansRank>();
		for(ClansPlayer p : players) {
			this.players.put(p.getUniqueId(), ClansRank.RECRUIT);
		}
		
		this.territory = new ArrayList<Chunk>();
	}
	
	public Clan(String name, String founder, UUID uuid, HashMap<UUID, ClansRank> players) {
		this.name = name;
		this.founder = founder;
		this.uuid = uuid;
		this.warpoints = new HashMap<UUID, Long>();
		this.clanRelations = new HashMap<UUID, ClanRelations>();
		this.home = null;
		
		this.players = players;
		this.territory = new ArrayList<Chunk>();
	}
	
	public void announceToClan(String text, ClansPlayer... exclude) {
		for(ClansPlayer o : this.getOnlinePlayers().keySet()) {
			boolean e = false;
			for(ClansPlayer ex : exclude) {
				if(o.getUniqueId() == ex.getUniqueId()) e = true;
			}
			if(!e)
				o.sendMessageWithPrefix("Clan", text);
		}
	}
	
	public void announceToClan(String text, boolean prefix, ClansPlayer... exclude) {
		for(ClansPlayer o : this.getOnlinePlayers().keySet()) {
			boolean e = false;
			for(ClansPlayer ex : exclude) {
				if(o.getUniqueId() == ex.getUniqueId()) e = true;
			}
			if(!e && prefix)
				o.sendMessageWithPrefix("Clan", text);
			if(!e && !prefix)
				o.sendMessage(text);
		}
	}
	
	public boolean isServerClan() {
		for(ServerClan type : ServerClan.values()) {
			if(type.getName().equalsIgnoreCase(this.getName())) return true;
		}
		return false;
	}
	
	public void shoutToRelation(ClanRelations relation, Player shouter, String message) {
		ClanRelations r = relation;
		ChatColor playerColor, messageColor;
		String prefix = me.rey.clans.utils.Text.getPrefix(shouter);
		
		switch(r) {
		case SELF:
			playerColor = r.getPlayerColor(); messageColor = r.getClanColor();
			break;
		default:
			playerColor = r.getClanColor(); messageColor = r.getPlayerColor();
			break;
		}
		
		String text = Text.color(prefix + playerColor + (r.getId() == ClanRelations.SELF.getId() ? "" : this.getName() + " ") + shouter.getName() + " " + messageColor + message);
		
		for(UUID uuid : this.getRelations().keySet()) {
			if(this.getClanRelation(uuid).getId() != r.getId()) continue;
			
			Clan related = Main.getInstance().getSQLManager().getClan(uuid);
			
			for(ClansPlayer toShout : related.getOnlinePlayers().keySet()) {
				toShout.getPlayer().sendMessage(text);
			}
		}
		
		for(ClansPlayer inside : this.getOnlinePlayers().keySet()) {
			inside.getPlayer().sendMessage(text);
		}
		
	}
	
	public Location getHome() {
		return this.home;
	}
		
	public void setHome(Location home) {
		this.home = home;
	}
	
	public boolean compare(Clan clan) {
		if(clan == null)
			return false;
		return this.getUniqueId().equals(clan.getUniqueId());
	}
	
	public long getEnergy() {
		return energy;
	}
	
	public double getEnergyDays() {
		return this.getEnergy() * References.MAX_ENERGY_DAYS / References.MAX_ENERGY;
	}
	
	public String getEnergyString() {
		double count = this.getEnergyDays();
		String countString = String.format("%.1f", count < 1.0 ? count * 24 : count);
		return count <= 0 ? "N/A" : countString + " " + (count < 1.0 ? "Hours" : "Days");
	}
	
	public Clan setEnergy(long energy) {
		this.energy = energy;
		return this;
	}
	
	public String getFounder() {
		return founder;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Clan setName(String name) {
		this.name = name;
		return this;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	
	/*
	 *  PLAYERS SECTION
	 */
	
	public HashMap<UUID, ClansRank> getPlayers(boolean withFakes){
		HashMap<UUID, ClansRank> clone = new HashMap<>(), toSend = new HashMap<UUID, ClansRank>();
		List<UUID> exclude = new ArrayList<>();
		this.players.forEach((k, v) -> clone.put(k, v));
		
		if(withFakes) {
			for(UUID player : Main.adminFakeClans.keySet()) {
				if(Main.adminFakeClans.get(player).equals(this.getUniqueId()))
					clone.put(player, ClansRank.ADMIN);
			}
			
			for(UUID uuid : clone.keySet()) {
				if(new ClansPlayer(uuid).isInFakeClan() && new ClansPlayer(uuid).getRealClan() != null && new ClansPlayer(uuid).getRealClan().compare(this))
					exclude.add(uuid);
			}
		}
		
		for(UUID uuid : clone.keySet()) {
			if(exclude.contains(uuid)) continue;
			toSend.put(uuid, clone.get(uuid));
		}
		return toSend;
	}
	
	public HashMap<UUID, ClansRank> getPlayers(){
		return getPlayers(true);
	}
	
	public boolean hasMaxMembers() {
		return this.getPlayers(false).size() >= References.MAX_MEMBERS;
	}
	
	public ArrayList<UUID> getPlayersFromRank(ClansRank rank, boolean fakes){
		ArrayList<UUID> players = new ArrayList<>();
		for(UUID p : this.getPlayers(fakes).keySet()) {
			if(this.getPlayerRank(p, fakes) == rank)
				players.add(p);
		}
		return players;
	}
	
	public ArrayList<UUID> getPlayersFromRank(ClansRank rank){
		return getPlayersFromRank(rank, true);
	}
	
	public boolean setRank(UUID player, ClansRank rank) {
		if(isInClan(player)) {
			this.players.put(player, rank);
			return true;
		}
		return false;
	}
	
	public ClansRank getPlayerRank(UUID player, boolean fakes) {
		if(isInClan(player, fakes)) {
			for(UUID cp : this.getPlayers(fakes).keySet()) {
				if(cp.equals(player))
					return this.getPlayers(fakes).get(cp);
			}
		}
		return null;
	}

	public ClansRank getPlayerRank(UUID player) {
		return getPlayerRank(player, true);
	}
	
	public boolean isInClan(UUID player, boolean fakes) {
		return getMatchingPlayer(player, fakes) != null;
	}
	
	public boolean isInClan(UUID player) {
		return isInClan(player, true);
	}
	
	public boolean isInClan(String name) {
		return getPlayer(name) != null;
	}
	
	public ClansPlayer getPlayer(String name) {
		for(UUID uuid : this.getPlayers().keySet()) {
			ClansPlayer cp = new ClansPlayer(uuid);
			if(cp.isOnline() && cp.getPlayer().getName().equalsIgnoreCase(name)) return cp;
			if(!cp.isOnline() && cp.getOfflinePlayer().getName().equalsIgnoreCase(name)) return cp;
		}
		return null;
	}
	
	public boolean kickPlayer(UUID player) {
		if(isInClan(player)) {
			if(new ClansPlayer(player).isInFakeClan() && new ClansPlayer(player).getFakeClan().compare(this))
				return false;
			this.players.remove(player);
			return true;
		}
		return false;
	}
	
	public boolean demote(UUID player) {
		if(isInClan(player)) {
			ClansRank rank = null;
			int index = getPlayerRank(player).ordinal();
			
			for(ClansRank r : ClansRank.values()) {
				if(r == ClansRank.NONE) continue;
				if(r.ordinal() == index-1) {
					rank = r;
					break;
				}
			}
			
			if(rank != null) {
				this.setRank(player, rank);
				return true;
			}
			
		}
		return false;
	}
	
	public boolean promote(UUID player) {
		if(isInClan(player)) {
			ClansRank rank = null;
			int index = getPlayerRank(player).ordinal();
			
			for(ClansRank r : ClansRank.values()) {
				if(r == ClansRank.NONE) continue;
				if(r.ordinal() == index+1) {
					rank = r;
					break;
				}
			}
			
			if(rank != null) {
				this.setRank(player, rank);
				return true;
			}
			
		}
		return false;
	}
	
	public HashMap<ClansPlayer, ClansRank> getOnlinePlayers(boolean fakes){
		HashMap<ClansPlayer, ClansRank> online = new HashMap<ClansPlayer, ClansRank>();
		for(UUID uuid : this.getPlayers(fakes).keySet()) {
			ClansPlayer cp = new ClansPlayer(uuid);
			if(cp.getPlayer() != null && cp.getPlayer().isOnline())
				online.put(cp, this.getPlayers(fakes).get(uuid));
		}
		return online;
	}
	
	public HashMap<ClansPlayer, ClansRank> getOnlinePlayers(){
		return getOnlinePlayers(true);
	}
	
	public void addPlayer(UUID uuid, ClansRank rank) {
		if(getMatchingPlayer(uuid) != null) return;
		
		this.players.put(uuid, rank);
	}
	
	public ClansPlayer getMatchingPlayer(UUID player, boolean fakes) {
		for(UUID cp : this.getPlayers(fakes).keySet()){
			if(cp.equals(player)) {
				return new ClansPlayer(cp);
			}
		}
		return null;
	}
	
	public ClansPlayer getMatchingPlayer(UUID player) {
		return this.getMatchingPlayer(player, true);
	}
	
	/*
	 *  TERRITORY SECTION
	 */
	
	public ArrayList<Chunk> getTerritory(){
		return territory;
	}
	
	public boolean hasMaxTerritory() {
		return this.getTerritory().size() >= References.MAX_TERRITORY || this.getTerritory().size() >= getPossibleTerritory();
	}
	
	public int getPossibleTerritory() {
		return this.getPlayers(false).size() + 2;
	}
	
	public boolean addTerritory(Chunk chunk) {
		if(this.territory.contains(chunk)) return false;
		Main.getInstance().territory.put(chunk, this.getUniqueId());
		
		this.territory.add(chunk);
		return true;
	}
	
	public boolean removeTerritory(Chunk chunk) {
		if(!this.territory.contains(chunk)) return false;
		if(Main.getInstance().getClanFromTerritory(chunk) != null)
			Main.getInstance().removeTerritory(chunk);
		
		this.territory.remove(chunk);
		return true;
	}
	
	public void addTerritory(ArrayList<Chunk> chunks) {
		for(Chunk chunk : chunks) {
			this.addTerritory(chunk);
		}
	}
	
	public void unclaimAll() {
		Iterator<Chunk> chunks = this.getTerritory().iterator();
		while(chunks.hasNext()) {
			Chunk next = chunks.next();
			if(Main.getInstance().getClanFromTerritory(next) != null)
				Main.getInstance().removeTerritory(next);
		}
		this.territory.clear();
	}
	
	/*
	 *  WARPOINTS SECTION
	 */
	
	public long setWarpoint(UUID clan, long warpoints) {
		if(warpoints == 0 && this.warpoints.containsKey(clan)) {
			this.warpoints.remove(clan);
			return 0;
		}
		this.warpoints.put(clan, warpoints);
		return warpoints;
	}
	
	public long getWarpointsOnClan(UUID clan) {
		if(!this.warpoints.containsKey(clan)) return 0;
		return this.warpoints.get(clan);
	}
	
	public HashMap<UUID, Long> getWarpoints(){
		return this.warpoints;
	}
	
	public void setWarpointsMap(HashMap<UUID, Long> wps) {
		this.warpoints = wps;
	}
	
	/*
	 *  CLAN RELATIONS SECTION
	 */
	
	public void setRelation(UUID clan, ClanRelations relation) {
		if(!relation.shouldSave()) return;
		this.clanRelations.put(clan, relation);
	}
	
	public void removeRelation(UUID clan) {
		this.clanRelations.remove(clan);
	}
	
	public ClanRelations getClanRelation(UUID clan) {
		if(clan.equals(this.getUniqueId())) return ClanRelations.SELF;

		if(!this.clanRelations.containsKey(clan)) {
			if(getWarpointsOnClan(clan) >= 10 || getWarpointsOnClan(clan) <= -10)
				return ClanRelations.ENEMY;
			return ClanRelations.NEUTRAL;
		}
		return this.clanRelations.get(clan);
	}
	
	public HashMap<UUID, ClanRelations> getRelations() {
		return this.clanRelations;
	}
	
	public boolean hasMaxAllies() {
		int allies = 0;
		for(UUID uuid : this.getRelations().keySet()) {
			if(getClanRelation(uuid).equals(ClanRelations.ALLY))
				allies++;
		}
		return allies >= References.MAX_ALLIES;
	}
	
//  ------------------------------------------- REMOVED
//	public boolean hasMaxTruces() {
//		int allies = 0;
//		for(UUID uuid : this.getRelations().keySet()) {
//			if(getClanRelation(uuid).equals(ClanRelations.TRUCE))
//				allies++;
//		}
//		return allies >= References.MAX_TRUCES;
//	}
//	
	public void setRelationsMap(HashMap<UUID, ClanRelations> relations) {
		this.clanRelations = relations;
	}
	
	/*
	 * SIEGE SYSTEM
	 */
	
	public boolean isBeingSieged() {
		return getClansSiegingSelf() != null && !getClansSiegingSelf().isEmpty();
	}
	
	public ArrayList<Siege> getClansSiegingSelf() {
		ArrayList<Siege> siegersOnSelf = new ArrayList<Siege>();
		for(UUID siegers : Siege.sieges.keySet()) {
			ArrayList<Siege> siegerSieging = Siege.sieges.get(siegers) == null ? new ArrayList<>() : Siege.sieges.get(siegers);
			Iterator<Siege> siegerIterator = siegerSieging.iterator();
			while(siegerIterator.hasNext()) {
				Siege found = siegerIterator.next();
				if(found.getClanSieged().getUniqueId().equals(this.getUniqueId())) {
					siegersOnSelf.add(found);
				}
			}
		}
		return siegersOnSelf;
	}
	
	public boolean isSiegingOther() {
		return getClansSiegedBySelf() != null && !getClansSiegedBySelf().isEmpty();
	}
	
	public ArrayList<Siege> getClansSiegedBySelf(){
		return Siege.sieges.get(this.getUniqueId()) == null ? new ArrayList<>() : Siege.sieges.get(this.getUniqueId());
	}
}

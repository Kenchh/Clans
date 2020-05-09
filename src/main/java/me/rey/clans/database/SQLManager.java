package me.rey.clans.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.clans.ServerClan;

public class SQLManager {
	
	private final Main plugin;
	private final ConnectionPoolManager pool;
	private final String clansDataTable, clansPlayerDataTable, clansSettingsTable;
	private final String safeZoneSetting = "safezone";
	
	public SQLManager(Main plugin) {
		this.plugin = plugin;
		pool = new ConnectionPoolManager(plugin);
		this.clansDataTable = this.plugin.getConfig().getConfigurationSection("mysql").getString("clans_data_table");
		this.clansPlayerDataTable = this.plugin.getConfig().getConfigurationSection("mysql").getString("clans_player_data_table");
		this.clansSettingsTable = this.plugin.getConfig().getConfigurationSection("mysql").getString("clans_settings_table");
		makeTable();
	}
	
	public void onDisable() {
		pool.closePool();
	}
	
	private void makeTable() {
		Connection conn = null;
		PreparedStatement ps = null, ps2 = null, ps3 = null;
		
		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `" + clansDataTable + "` " +
						"(" + 
						"uuid TEXT, name TEXT, founder TEXT, energy BIGINT, home TEXT, members JSON, territory JSON, relations JSON, warpoints JSON" +
						")"
					);
			ps.executeUpdate();
			
			ps2 = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `" + clansPlayerDataTable + "` " +
						"(" + 
						"uuid TEXT, gold INT, clan TEXT" +
						")"
			);
			ps2.executeUpdate();
			
			ps3 = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `" + clansSettingsTable + "` " +
						"(" + 
						"name TEXT, value JSON" +
						")"
			);
			ps3.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, null);
			pool.close(null, ps2, null);
			pool.close(null, ps3, null);
		}
	}
	
	public boolean settingExists(String setting) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			String stmt = "SELECT * FROM " + clansSettingsTable + " WHERE name=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, setting);
			res = ps.executeQuery();
			
			if(res.next()) {
				return true;
			} 
			
			return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
		}
		return false;
	}
	
	public boolean createSetting(String setting) {
		Connection conn = null;
		PreparedStatement ps = null, insert = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			
			String stmt = "SELECT * FROM " + clansSettingsTable + " WHERE name=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, setting);
			res = ps.executeQuery();
			
			res.next();
			if(!settingExists(setting)) {
				String stmt2 = "INSERT INTO " + clansSettingsTable
						+ "(name,value) VALUE(?,?)";
				insert = conn.prepareStatement(stmt2);
				insert.setString(1, setting);
				insert.setString(2, null);
				insert.executeUpdate();
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
			pool.close(null, insert, null);
		}
		return false;
	}
	
	public String getSetting(String setting) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			createSetting(setting);
			conn = pool.getConnection();
			
			String stmt = "SELECT * FROM " + clansSettingsTable + " WHERE name=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, setting);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				return rs.getString("value");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, rs);
		}
		return null;
	}
	
	public boolean clanExists(String name) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			String stmt = "SELECT * FROM " + clansDataTable + " WHERE name=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, name);
			res = ps.executeQuery();
			
			if(res.next()) {
				return true;
			} 
			
			return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
		}
		return false;
	}
	
	public boolean clanExists(UUID uuid) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			String stmt = "SELECT * FROM " + clansDataTable + " WHERE uuid=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, uuid.toString());
			res = ps.executeQuery();
			
			if(res.next()) {
				return true;
			} 
			
			return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
		}
		return false;
	}
	
	public boolean playerExists(UUID uuid) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			String stmt = "SELECT * FROM " + clansPlayerDataTable + " WHERE uuid=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, uuid.toString());
			res = ps.executeQuery();
			
			if(res.next()) {
				return true;
			} 
			
			return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
		}
		return false;
	}
	
	public boolean playerExists(Player player) {
		return playerExists(player.getUniqueId());
	}
	
	public boolean playerExists(String name) {
		return getPlayerFromName(name) != null;
	}
	
	public boolean createPlayer(Player player) {
		return createPlayer(player.getUniqueId());
	}
	
	public boolean createPlayer(UUID player) {
		if(playerExists(player)) return false;

		Connection conn = null;
		PreparedStatement ps = null, insert = null;
		ResultSet res = null;

		try {
			conn = pool.getConnection();

			String stmt = "SELECT * FROM " + clansPlayerDataTable + " WHERE uuid=?";
			ps = conn.prepareStatement(stmt);

			ps.setString(1, player.toString());
			res = ps.executeQuery();

			res.next();

			String stmt2 = "INSERT INTO " + clansPlayerDataTable
					+ "(uuid,gold,clan) VALUE(?,?,?)";
			insert = conn.prepareStatement(stmt2);
			insert.setString(1, player.toString());
			insert.setInt(2, 16000);
			insert.setString(3, null);
			insert.executeUpdate();

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("clan", null);
			data.put("gold", 16000);
			Main.playerdata.put(player, data);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
			pool.close(null, insert, null);
		}
		return false;
	}
	
	public boolean createClan(UUID uuid, String name, String founder, Player leader) {
		Connection conn = null;
		PreparedStatement ps = null, insert = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			
			String stmt = "SELECT * FROM " + clansDataTable + " WHERE name=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, name);
			res = ps.executeQuery();
			
			res.next();
			if(!clanExists(name)) {
				HashMap<Integer, ArrayList<String>> members = new HashMap<>();
				if(leader != null) {
					ArrayList<String> array = new ArrayList<String>();
					array.add(leader.getUniqueId().toString());
					members.put(ClansRank.LEADER.getId(), array);
				}
 				
				String stmt2 = "INSERT INTO " + clansDataTable
						+ "(uuid,name,founder,energy,home,members,territory,relations,warpoints) VALUE(?,?,?,?,?,?,?,?,?)";
				insert = conn.prepareStatement(stmt2);
				insert.setString(1, uuid.toString());
				insert.setString(2, name);
				insert.setString(3, founder);
				insert.setInt(4, 10000);
				insert.setString(5, null);
				insert.setString(6, new JSONObject(members).toJSONString());
				insert.setString(7, null);
				insert.setString(8, null);
				insert.setString(9, null);
				insert.executeUpdate();
				
				if(leader != null) {
					createPlayer(leader);
					setPlayerData(leader.getUniqueId(), "clan", uuid.toString());
				}
				
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
			pool.close(null, insert, null);
		}
		
		boolean existsInEnergy = false;
		Clan self = getClan(uuid);
		for(Clan clan : Main.clans) {
			if(clan.compare(self)) existsInEnergy = true;
		}
		
		if(!existsInEnergy)
			Main.clans.add(getClan(uuid));
		
		
		return true;
	}
	
	public boolean createClan(UUID uuid, String name, Player founder) {
		return this.createClan(uuid, name, founder.getName(), founder);
	}

	public boolean saveClan(Clan clan) {
		Connection conn = null;
		PreparedStatement ps = null, insert = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			
			String stmt = "SELECT * FROM " + clansDataTable + " WHERE name=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, clan.getName());
			res = ps.executeQuery();
			
			res.next();
			if(!clanExists(clan.getName())) {
				createClan(clan.getUniqueId(), clan.getName(), null);
			}
			
			UUID uuid = clan.getUniqueId();
			String home = clan.getHome() == null ? null : "";

			/*
			 * CLAN HOME
			 */
			if(clan.getHome() != null)
				home+= clan.getHome().getBlockX() + ";" + clan.getHome().getBlockY() +  ";" + clan.getHome().getBlockZ();
			
			/*
			 * MEMBERS
			 */
			HashMap<Integer, ArrayList<String>> members = new HashMap<>();
			for(UUID u : clan.getPlayers(false).keySet()) {
				ClansPlayer m = new ClansPlayer(u);
				ClansRank rank = clan.getPlayers(false).get(u);
				
				ArrayList<String> membersUUID = members.get(rank.getId()) == null ? new ArrayList<>() : members.get(rank.getId());
				membersUUID.add(m.getUniqueId().toString());
				
				members.put(rank.getId(), membersUUID);
				this.setPlayerData(m.getUniqueId(), "clan", clan.getUniqueId().toString());
			}
			
			/*
			 * TERRITORY
			 */
			ArrayList<String> territory = new ArrayList<String>();
			for(Chunk chunk : clan.getTerritory()) {
				territory.add(String.format("%s;%s", chunk.getX(), chunk.getZ()));
			}
			HashMap<String, ArrayList<String>> chunks = new HashMap<>();
			chunks.put("territory", territory);
			
			/*
			 *  CLAN RELATIONS
			 */
			HashMap<Integer, ArrayList<String>> relations = new HashMap<>();
			for(UUID related : clan.getRelations().keySet()) {
				ClanRelations relation = clan.getClanRelation(related);
				if(!relation.shouldSave()) continue;
				
				ArrayList<String> clansUUID = relations.get(relation.getId()) == null ? new ArrayList<>() : relations.get(relation.getId());
				clansUUID.add(related.toString());
				
				relations.put(relation.getId(), clansUUID);
			}
			
			/*
			 * WARPOINTS
			 */
			HashMap<String, HashMap<String, Long>> warpoints = new HashMap<>();
			HashMap<String, Long> positive = new HashMap<>(), negative = new HashMap<>();
			for(UUID opponent : clan.getWarpoints().keySet()) {
				long wp = clan.getWarpointsOnClan(opponent);
				if(wp >= 0) {
					positive.put(opponent.toString(), clan.getWarpointsOnClan(opponent));
					warpoints.put("positive", positive);	
				} else {
					negative.put(opponent.toString(), clan.getWarpointsOnClan(opponent));
					warpoints.put("negative", negative);
				}
			}
			
			this.setClanData(uuid, "name", clan.getName());
			this.setClanData(uuid, "founder", clan.getFounder());
			this.setClanData(uuid, "energy", clan.getEnergy());
			this.setClanData(uuid, "home", home);
			this.setClanData(uuid, "members", new JSONObject(members).toJSONString());
			this.setClanData(uuid, "territory", new JSONObject(chunks).toJSONString());
			this.setClanData(uuid, "relations", new JSONObject(relations).toJSONString());
			this.setClanData(uuid, "warpoints", new JSONObject(warpoints).toJSONString());

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
			pool.close(null, insert, null);
		}
		return false;
	}

	public Clan getClan(UUID uuid) {
		Connection conn = null;
		PreparedStatement ps = null, insert = null;
		ResultSet res = null;
		
		try {
			if(!clanExists(uuid)) return null;
			
			conn = pool.getConnection();
			String stmt = "SELECT * FROM " + clansDataTable + " WHERE uuid=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, uuid.toString());
			res = ps.executeQuery();

			while(res.next()) {
				String name, founder;
				long energy = 0;
				HashMap<UUID, ClansRank> members = new HashMap<>();
				ArrayList<Chunk> territory = new ArrayList<>();
				HashMap<UUID, ClanRelations> relations = new HashMap<>();
				HashMap<UUID, Long> warpoints = new HashMap<>();
				Location home = null;
				
				// setting name
				name = res.getString("name");
				
				// setting founder
				founder = res.getString("founder");
				
				// setting energy;
				energy = res.getLong("energy");
				
				// setting home
				if(res.getString("home") != null) {
					String[] coords = res.getString("home").split(";");
					int x = Integer.parseInt(coords[0]), y = Integer.parseInt(coords[1]), z = Integer.parseInt(coords[2]);
					home = new Location(Main.getInstance().getClansWorld(), x, y, z);
				}
				
				// setting members
				JSONObject objMembers = (JSONObject) new JSONParser().parse(res.getString("members"));
				for(Object o : objMembers.keySet()) {
					int rankId = Integer.parseInt((String) o);
					ClansRank rank = ClansRank.getRankFromId(rankId);
					
					JSONArray jsonMembers = (JSONArray) objMembers.get((String) o);
					for(Object string : jsonMembers) {
						UUID memberUuid = UUID.fromString((String) string);
						
						members.put(memberUuid, rank);
					}
				}
				
				//territory
				if(res.getString("territory") != null) {
					JSONObject objTerritory = (JSONObject) new JSONParser().parse(res.getString("territory"));
					JSONArray arrayTerritory = (JSONArray) objTerritory.get("territory");
					for(Object entry : arrayTerritory) {
						String[] coords = ((String) entry).split(";");
						Chunk chunk = Main.getInstance().getClansWorld().getChunkAt(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
						
						territory.add(chunk);
					}
				}
				
				
				//relations
				if(res.getString("relations") != null) {
					JSONObject objRelations = (JSONObject) new JSONParser().parse(res.getString("relations"));
					for(Object o : objRelations.keySet()) {
						int relationId = Integer.parseInt((String) o);
						ClanRelations relation = ClanRelations.getRelationFromId(relationId);
						
						JSONArray jsonRelations = (JSONArray) objRelations.get((String) o);
						for(Object string : jsonRelations) {
							UUID clanUuid = UUID.fromString((String) string);
							relations.put(clanUuid, relation);
						}
					}
				}
				
				//warpoints
				if(res.getString("warpoints") != null) {
					JSONObject objWP = (JSONObject) new JSONParser().parse(res.getString("warpoints"));
					for(Object o : objWP.keySet()) {
						
						JSONObject entries = (JSONObject) objWP.get((String) o);
						for(Object string : entries.keySet()) {
							UUID clanUuid = UUID.fromString((String) string);
							long warpoint = (long) entries.get((String) string);
							warpoints.put(clanUuid, warpoint);
						}
					}
				}
				
				Clan toGive = new Clan(name, founder, uuid, members);
				toGive.setEnergy(energy);
				toGive.setHome(home);
				toGive.setWarpointsMap(warpoints);
				toGive.setRelationsMap(relations);
				toGive.addTerritory(territory);
				return toGive;
			}
			
			return null;
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
			pool.close(null, insert, null);
		}
		return null;
	}
	
	public Clan getClan(String name) {
		Connection conn = null;
		PreparedStatement ps = null, insert = null;
		ResultSet res = null;
		
		try {
			if(!clanExists(name)) return null;
			
			conn = pool.getConnection();
			String stmt = "SELECT * FROM " + clansDataTable + " WHERE name=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, name);
			res = ps.executeQuery();
			
			while(res.next()) {
				UUID uuid;
				String founder, namex;
				long energy = 0;
				HashMap<UUID, ClansRank> members = new HashMap<>();
				ArrayList<Chunk> territory = new ArrayList<>();
				HashMap<UUID, ClanRelations> relations = new HashMap<>();
				HashMap<UUID, Long> warpoints = new HashMap<>();
				Location home = null;
				
				// setting name
				namex = res.getString("name");
				
				// setting uuid
				uuid = UUID.fromString(res.getString("uuid"));
				
				// setting founder
				founder = res.getString("founder");
				
				// setting energy;
				energy = res.getLong("energy");
				
				// setting home
				if(res.getString("home") != null) {
					String[] coords = res.getString("home").split(";");
					int x = Integer.parseInt(coords[0]), y = Integer.parseInt(coords[1]), z = Integer.parseInt(coords[2]);
					home = new Location(Main.getInstance().getClansWorld(), x, y, z);
				}
				
				
				// setting members
				JSONObject objMembers = (JSONObject) new JSONParser().parse(res.getString("members"));
				for(Object o : objMembers.keySet()) {
					int rankId = Integer.parseInt((String) o);
					ClansRank rank = ClansRank.getRankFromId(rankId);
					
					JSONArray jsonMembers = (JSONArray) objMembers.get((String) o);
					for(Object string : jsonMembers) {
						UUID memberUuid = UUID.fromString((String) string);
						
						members.put(memberUuid, rank);
					}
				}
				
				//territory
				if(res.getString("territory") != null) {
					JSONObject objTerritory = (JSONObject) new JSONParser().parse(res.getString("territory"));
					JSONArray arrayTerritory = (JSONArray) objTerritory.get("territory");
					for(Object entry : arrayTerritory) {
						String[] coords = ((String) entry).split(";");
						Chunk chunk = Main.getInstance().getClansWorld().getChunkAt(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
						
						territory.add(chunk);
					}
				}
				
				
				//relations
				if(res.getString("relations") != null) {
					JSONObject objRelations = (JSONObject) new JSONParser().parse(res.getString("relations"));
					for(Object o : objRelations.keySet()) {
						int relationId = Integer.parseInt((String) o);
						ClanRelations relation = ClanRelations.getRelationFromId(relationId);
						
						JSONArray jsonRelations = (JSONArray) objRelations.get((String) o);
						for(Object string : jsonRelations) {
							UUID clanUuid = UUID.fromString((String) string);
							relations.put(clanUuid, relation);
						}
					}
				}
				
				//warpoints
				if(res.getString("warpoints") != null) {
					JSONObject objWP = (JSONObject) new JSONParser().parse(res.getString("warpoints"));
					for(Object o : objWP.keySet()) {
						
						JSONObject entries = (JSONObject) objWP.get((String) o);
						for(Object string : entries.keySet()) {
							UUID clanUuid = UUID.fromString((String) string);
							long warpoint = (long) entries.get((String) string);
							warpoints.put(clanUuid, warpoint);
						}
					}
				}
				
				Clan toGive = new Clan(namex, founder, uuid, members);
				toGive.setEnergy(energy);
				toGive.setHome(home);
				toGive.setWarpointsMap(warpoints);
				toGive.setRelationsMap(relations);
				toGive.addTerritory(territory);
				return toGive;
			}
			
			return null;
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
			pool.close(null, insert, null);
		}
		return null;
	}
	
	public void deleteClan(UUID uuid) {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			Clan clan = Main.getInstance().getClan(uuid);
			if(clan == null) return;


			Clan toRemove = null;
			for(Clan clanL : Main.clans) {
				if(uuid.equals(clanL.getUniqueId())) toRemove = clan;
			}

			if(toRemove != null)
				Main.clans.remove(toRemove);


			for(Chunk chunk : clan.getTerritory()) {
				Main.getInstance().territory.remove(chunk);
			}

			ArrayList<UUID> playersToRemove = new ArrayList<UUID>();
			for(UUID player : Main.adminFakeClans.keySet()) {
				if(Main.adminFakeClans.get(player).equals(clan.getUniqueId()))
					playersToRemove.add(player);
			}

			for(UUID player : playersToRemove) {
				Main.adminFakeClans.remove(player);
			}

			if(clan.getHome() != null)
				clan.getHome().getBlock().setType(Material.AIR);

			conn = pool.getConnection();
			String stmt = "DELETE FROM " + clansDataTable + " WHERE uuid=?";
			ps = conn.prepareStatement(stmt);

			ps.setString(1, uuid.toString());
			ps.executeUpdate();

			for(UUID cp : clan.getPlayers().keySet()) {
				this.setPlayerData(cp, "clan", null);
			}

			for(UUID uR : clan.getRelations().keySet()) {
				Clan related = Main.getInstance().getClan(uR);
				related.removeRelation(clan.getUniqueId());
				saveClan(related);
			}

			for(UUID uR : clan.getWarpoints().keySet()) {
				Clan enemy = Main.getInstance().getClan(uR);
				enemy.setWarpoint(clan.getUniqueId(), 0);
				saveClan(enemy);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, null);
		}

	}
	
	public void setClanData(UUID uuid, String column, Object data) {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = pool.getConnection();

			String stmt = "UPDATE " + clansDataTable + " SET " + column + "=?  WHERE uuid=?";
			ps = conn.prepareStatement(stmt);

			ps.setObject(1, data);
			ps.setString(2, uuid.toString());
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, null);
		}
	}
	
	public Object getClanData(UUID uuid, String column) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			String stmt = "SELECT * FROM " + clansDataTable + " WHERE uuid=?";
			ps = conn.prepareStatement(stmt);

			ps.setString(1, uuid.toString());
			rs = ps.executeQuery();

			while(rs.next()) {
				return rs.getObject(column);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, rs);
		}
		return null;
	}

	public void setPlayerData(UUID player, String column, Object data) {
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			createPlayer(player);
			conn = pool.getConnection();

			String stmt = "UPDATE " + clansPlayerDataTable + " SET " + column + "=?  WHERE uuid=?";
			ps = conn.prepareStatement(stmt);

			ps.setObject(1, data);
			ps.setString(2, player.toString());
			ps.executeUpdate();

			Main.playerdata.get(player).replace(column, data);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, null);
		}
	}

	public HashMap<UUID, HashMap<String, Object>> getAllPlayerData() {

		HashMap<UUID, HashMap<String, Object>> pd = new HashMap<UUID, HashMap<String, Object>>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			String stmt = "SELECT * FROM " + clansPlayerDataTable + " WHERE uuid IS NOT NULL";
			ps = conn.prepareStatement(stmt);

			rs = ps.executeQuery();

			while (rs.next()) {
				HashMap<String, Object> data = new HashMap<String, Object>();

				data.put("clan", rs.getObject("clan"));
				data.put("gold", rs.getObject("gold"));

				pd.put(UUID.fromString((String) rs.getObject("uuid")), data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, rs);
		}

		return pd;
	}

	/*
	public Object getPlayerData(UUID player, String column) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			createPlayer(player);
			conn = pool.getConnection();
			UUID uuid = player;
			
			String stmt = "SELECT * FROM " + clansPlayerDataTable + " WHERE uuid=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, uuid.toString());
			rs = ps.executeQuery();
			
			while(rs.next()) {
				return rs.getObject(column);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, rs);
		}
		return null;
	}


	public Object getPlayerData(Player player, String column) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			createPlayer(player);
			conn = pool.getConnection();
			UUID uuid = player.getUniqueId();
			
			String stmt = "SELECT * FROM " + clansPlayerDataTable + " WHERE uuid=?";
			ps = conn.prepareStatement(stmt);
			
			ps.setString(1, uuid.toString());
			rs = ps.executeQuery();
			
			while(rs.next()) {
				return rs.getObject(column);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, rs);
		}
		return null;
	}
	*/
	
	public void savePlayer(UUID player) {
		if(hasClan(player)) {
			Clan old = new ClansPlayer(player).getRealClan();
			if(Main.getInstance().getClan(old.getUniqueId()).getMatchingPlayer(player) == null) {
				this.setPlayerData(player, "clan", null);
				Main.playerdata.get(player).replace("clan", null);
			}
		}
	}
	
	public boolean hasClan(UUID player) {

		if(!playerExists(player)) return false;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		try {
			
			if(Main.playerdata.get(player).get("clan") == null)
				return false;
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
		}
		return false;
	}
	
	public OfflinePlayer getPlayerFromName(String name) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			String stmt = "SELECT * FROM " + clansPlayerDataTable + " WHERE uuid IS NOT NULL";
			ps = conn.prepareStatement(stmt);
			
			res = ps.executeQuery();
			
			while(res.next()) {
				UUID found = UUID.fromString((String) res.getString("uuid"));
				OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(found); 
				
				if(p != null && p.getName().equalsIgnoreCase(name))
					return p;
			} 
			
			return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
		}
		return null;
	}
	
	public HashMap<Chunk, UUID> loadTerritories(){
		
		HashMap<Chunk, UUID> chunks = new HashMap<Chunk, UUID>();
		for(Clan toLoad : getClans()) {

			for(Chunk chunk : toLoad.getTerritory()) {
				chunks.put(chunk, toLoad.getUniqueId());
			}
		}
		return chunks;
	}
	
	public ArrayList<Clan> getClans(){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		try {
			conn = pool.getConnection();
			String stmt = "SELECT * FROM " + clansDataTable + " WHERE uuid IS NOT NULL";
			ps = conn.prepareStatement(stmt);
			
			res = ps.executeQuery();
			ArrayList<Clan> clans = new ArrayList<>();
			
			while(res.next()) {
				UUID uuid = UUID.fromString(res.getString("uuid"));
				Clan clan = getClan(uuid);
				clans.add(clan);
			}
			
			
			return clans;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, res);
		}
		return new ArrayList<>();
	}
	
	public void loadServerClans() {
		for(ServerClan type : ServerClan.values()) {
			if(this.clanExists(type.getName())) continue;
			
			this.createClan(UUID.randomUUID(), type.getName(), "Server", null);
		}
	}
	
	public Clan getServerClan(ServerClan type) {
		loadServerClans();
		return this.getClan(type.getName());
	}
	
	public void saveSafeZones(Set<String> coords) {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			createSetting(safeZoneSetting);
			conn = pool.getConnection();
			
			String stmt = "UPDATE " + clansSettingsTable + " SET value=?  WHERE name=?";
			ps = conn.prepareStatement(stmt);
			
			/*
			 * COORDS
			 */
			int highest = 1;
			HashMap<Integer, ArrayList<String>> toSave = new HashMap<>();
			if(this.getSetting(safeZoneSetting) != null) {
				JSONObject setting = (JSONObject) new JSONParser().parse(this.getSetting(safeZoneSetting));
				for(Object o : setting.keySet()) {
					
					JSONArray keys = (JSONArray) setting.get((String) o);
					ArrayList<String> alreadySaved = new ArrayList<String>();
					for(int i = 0; i < keys.size(); i++) {
						alreadySaved.add((String) keys.get(i));
					}
					int index = Integer.parseInt((String) o);
					if(index >= highest) highest = index + 1;
					toSave.put(index, alreadySaved);
				}
			}
			
			ArrayList<String> tryingToSave = new ArrayList<String>(coords);
			toSave.put(highest, tryingToSave);
			
			ps.setString(1, new JSONObject(toSave).toJSONString());
			ps.setString(2, safeZoneSetting);
			ps.executeUpdate();
						
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		} finally {
			pool.close(conn, ps, null);
		}
	}
	
	public Set<String> getSafeZones(){
		Set<String> coords = new HashSet<String>();
		
		try { 
			if(this.getSetting(safeZoneSetting) != null) {
				JSONObject setting = (JSONObject) new JSONParser().parse(this.getSetting(safeZoneSetting));
				for(Object o : setting.keySet()) {
					
					JSONArray keys = (JSONArray) setting.get((String) o);
					for(int i = 0; i < keys.size(); i++) {
						coords.add((String) keys.get(i));
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return coords;
	}
	
}

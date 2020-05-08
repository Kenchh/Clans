package me.rey.clans.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.database.SQLManager;
import me.rey.clans.utils.References;
import me.rey.clans.utils.Text;

public class GuiClanInfo extends GuiEditable {

	private Clan clan;
	private ClansPlayer player;
	private SQLManager sql;
	
	public GuiClanInfo(Clan clan, ClansPlayer player) {
		super("", 6, JavaPlugin.getPlugin(Main.class));
		
		this.clan = clan;
		this.player = player;
		this.sql = Main.getInstance().getSQLManager();
	}

	@Override
	public void setup() {
		String TITLE_COLOR = "&a&l";

		long warpointsOnClan = !player.hasClan() ? 0 : player.getClan().getWarpointsOnClan(clan.getUniqueId()); 
		int maxChunks = clan.getPossibleTerritory() <= References.MAX_TERRITORY ? clan.getPossibleTerritory() : References.MAX_TERRITORY;

		List<String> lore = Arrays.asList("",
				"&eFounder &f" + clan.getFounder(),
				"&eMembers &f" + clan.getOnlinePlayers(false).size() + "/" + clan.getPlayers(false).size(),
				"&eTerritory &f" + clan.getTerritory().size() + "/" + maxChunks,
				"&eYour War Points &f" + warpointsOnClan,
				"", "&eEnergy &f" + clan.getEnergyString());
		Item info = new Item(Material.IRON_BARDING).setName(TITLE_COLOR + clan.getName()).setLore(lore);
		
		for(ClanRelations relation : ClanRelations.values()) {
			if(!relation.shouldSave()) continue;
			String rName = relation.getName().endsWith("y") ? relation.getName().substring(0, relation.getName().length()-1) + "ies" :
				relation.getName() + "s";
			String format = "&e" + rName + " &f";
			
			ArrayList<String> names = new ArrayList<String>();
			for(UUID related :clan.getRelations().keySet()) {
				if(clan.getClanRelation(related).equals(relation)) {
					names.add(Main.getInstance().getClan(related).getName());
				}
			}
			
			StringBuilder str = new StringBuilder();
			for(String name : names) {
				str.append(", ").append(name);
			}
			format += str.toString().equals(", ") || str.toString().equals("") ? "None" : str.toString().trim().replaceFirst(", ", "");
			info.addLore(format);
		}
		setItem(new GuiItem(info) {
			
			@Override
			public void onUse(Player player, ClickType type, int slot) {
				
			}
			
		}, 4);
		
		int[] playerPositions = {20, 21, 22, 23, 24, 29, 30, 31, 32 , 33, 39, 40, 41};
		int index = 0;
		/*
		 *  ONLINE PLAYERS
		 */
		for(int i = ClansRank.values().length-1; i >= 0; i--) {
			ClansRank rank = ClansRank.values()[i];
			if(rank == ClansRank.NONE || (index+1) >= playerPositions.length) continue;
			
			ArrayList<UUID> players = clan.getPlayersFromRank(rank, false);
			for(UUID uuid : players) {
				ClansPlayer cp = new ClansPlayer(uuid);
				if(cp.isInFakeClan() && cp.getFakeClan().compare(clan)) continue;
				if(cp.getPlayer()!= null && cp.getPlayer().isOnline()) {
					int position = playerPositions[index];
					
					String name = cp.getPlayer().getName();
					
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
					SkullMeta meta = (SkullMeta) head.getItemMeta();
					meta.setDisplayName(Text.color("&a&l" + name));
					meta.setLore(Arrays.asList("",
							Text.color("&eRole &f" + rank.getName()), ""));
					meta.setOwner(name);
					head.setItemMeta(meta);
					
					setItem(new GuiItem(head) {
						
						@Override
						public void onUse(Player player, ClickType type, int slot) {
							// ignore
						}
						
					}, position);
					index++;
				}
			}
		}
		
		/*
		 * OFFLINE PLAYERS
		 */
		for(int i = ClansRank.values().length-1; i >= 0; i--) {
			ClansRank rank = ClansRank.values()[i];
			if(rank == ClansRank.NONE || (index+1) >= playerPositions.length) continue;
			
			ArrayList<UUID> players = clan.getPlayersFromRank(rank, false);
			for(UUID uuid : players) {
				ClansPlayer cp = new ClansPlayer(uuid);
				if(cp.isInFakeClan() && cp.getFakeClan().compare(clan)) continue;
				if(cp.getPlayer() == null || !cp.getPlayer().isOnline()) {
					int position = playerPositions[index];
					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					
					String name = player.getName();
					
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 0);
					SkullMeta meta = (SkullMeta) head.getItemMeta();
					meta.setDisplayName(Text.color("&c&l" + name));
					meta.setLore(Arrays.asList("",
							Text.color("&eRole &f" + rank.getName()), ""));
					head.setItemMeta(meta);
					
					setItem(new GuiItem(head) {
						
						@Override
						public void onUse(Player player, ClickType type, int slot) {
							// ignore
						}
						
					}, position);
					index++;
				}	
			}
		}
		
		if(index < playerPositions.length-1) {
			for(int i = index; i < playerPositions.length; i++) {
				setItem(new GuiItem(new Item(Material.BARRIER).setName("&c&lEMPTY SLOT!")) {
					
					@Override
					public void onUse(Player player, ClickType type, int slot) {
						
					}
				}, playerPositions[i]);
			}
		}
	}

	@Override
	public void init() {
		
	}

}

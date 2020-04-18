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
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.clans.ClansRank;
import me.rey.clans.utils.References;
import me.rey.clans.utils.Text;

public class GuiClan extends GuiEditable {

	private Clan clan;
//	private SQLManager sql;
	private ClansRank rank;
	
	public GuiClan(Clan clan, ClansRank rank) {
		super("", 6, JavaPlugin.getPlugin(Main.class));
		this.clan = clan;
		this.rank = rank;
//		this.sql = Main.getInstance().getSQLManager();
	}

	@Override
	public void setup() {
		String TITLE_COLOR = "&a&l";
		
		
		// Invites item
		List<String> invitesLore = Arrays.asList("",
				"&7Clans have a max size of &e" + References.MAX_MEMBERS + "&7members",
				"&7You currently have &e" + clan.getPlayers(false).size() + "&7 members",
				"&7More members in your clan will allow you to",
				"&7claim more land, but it will also increase",
				"&7your Energy drain per minute.",
				"",
				"&eLeft Click &fInvite Player");
		setItem(new GuiItem(new Item(Material.PRISMARINE).setDurability(1).setName(TITLE_COLOR  + "Invites").setLore(invitesLore)) {
			
			@Override
			public void onUse(Player player, ClickType type, int slot) {
				// TODO Invites GUI
			}
		}, 0);
		
		// TERRITORY
		int maxChunks = clan.getPossibleTerritory() <= References.MAX_TERRITORY ? clan.getPossibleTerritory() : References.MAX_TERRITORY;
		List<String> territoryLore = Arrays.asList("",
				"&7Every land claim represents a 16x16 chunk",
				"&7Your clan can claim a maximum of &e" + maxChunks + "&7 chunks",
				"&7You currently have &e" + clan.getTerritory().size() + "&7 chunk(s) claimed",
				"&7Increase max claims with more clan members",
				"&7Energy cost will increase with more land claimed",
				"",
				"&eLeft Click &fClaim Land",
				"&eShift-Left Click &fUnclaim Land",
				"&eShift-Right Click &fUnclaim All Land");
		setItem(new GuiItem(new Item(Material.PRISMARINE).setName(TITLE_COLOR  + "Territory").setLore(territoryLore)) {
			
			@Override
			public void onUse(Player player, ClickType type, int slot) {
				
			}
		}, 2);
		
		// ENERGY
		List<String> energylore = Arrays.asList("",
				"&7Energy is the currency used to upkeep",
				"&7your clan. Energy drains over time and",
				"&7you will need to refill it at the NPC in",
				"&7the shops. More clan members and more land",
				"&7increased the rate energy drains at.",
				"",
				"&eEnergy &f" + clan.getEnergy() + "/" + References.MAX_ENERGY);
		setItem(new GuiItem(new Item(Material.SEA_LANTERN).setName(TITLE_COLOR  + "Energy").setLore(energylore)) {
			
			@Override
			public void onUse(Player player, ClickType type, int slot) {
				
			}
		}, 4);
		
		// LEAVE
		List<String> leaveLore = Arrays.asList("", "&eShift-Left Click &fLeave Clan", "&eShift-Right Click &fDisband Clan");
		setItem(new GuiItem(new Item(Material.PRISMARINE).setDurability(2).setName(TITLE_COLOR  + "Leave").setLore(leaveLore)) {
			
			@Override
			public void onUse(Player player, ClickType type, int slot) {
				ClansPlayer cp = new ClansPlayer(player.getUniqueId());
				switch(type) {
				case SHIFT_RIGHT:
					if(rank == ClansRank.LEADER) {
						cp.disbandClan();
						cp.getPlayer().closeInventory();
					}
					break;
				case SHIFT_LEFT:
					if(rank != ClansRank.LEADER) {
						clan.announceToClan("&s" + cp.getPlayer().getName() + " &rleft the Clan!", cp);
						cp.leaveClan();
						cp.getPlayer().closeInventory();
					}
					break;
				default:
					break;
				}
			}
			
		}, 6);
		
		// COMMANDS
		List<String> commandsLore = Arrays.asList("",
				"&e/c help &fLists Clans Commands",
				"&e/c ally <clan> &fRequest Ally",
				"&e/c truce <clan> &fRequest Truce",
				"&e/c neutral <clan> &fRevoke Ally or Truce",
				"&e/c sethome &fSet Home Bed",
				"&e/c home &fTeleport to Home Bed",
				"&e/c map &fGive yourself a World Map");
		setItem(new GuiItem(new Item(Material.LAVA_BUCKET).setName(TITLE_COLOR  + "Commands").setLore(commandsLore)) {
			
			@Override
			public void onUse(Player player, ClickType type, int slot) {
				
			}
		}, 8);
		
		
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
					int x = cp.getPlayer().getLocation().getBlockX(), y = cp.getPlayer().getLocation().getBlockY(),
							z = cp.getPlayer().getLocation().getBlockZ();
					
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
					SkullMeta meta = (SkullMeta) head.getItemMeta();
					meta.setDisplayName(Text.color("&a&l" + name));
					meta.setLore(Arrays.asList("",
							Text.color("&eRole &f" + rank.getName()),
							Text.color(String.format("&eLocation &f(%s, %s, %s)", x, y, z)), ""));
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

package me.rey.clans.playerdisplay;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.rey.clans.Main;
import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.packets.Nametag;
import me.rey.clans.utils.UtilFocus;

public class PlayerInfo implements Listener {
	
	private HashMap<Player, CustomScoreboard> scoreboardCache = new HashMap<>();
	
	final String[] scoreboardTitles = {
			"&b&lWarriors 1.0",
			"&f&lW&b&larriors 1.0",
			"&b&lW&f&la&b&lrriors 1.0",
			"&b&lWa&f&lr&b&lriors 1.0",
			"&b&lWar&f&lr&b&liors 1.0",
			"&b&lWarr&f&li&b&lors 1.0",
			"&b&lWarri&f&lo&b&lrs 1.0",
			"&b&lWarrio&f&lr&b&ls 1.0",
			"&b&lWarrior&f&ls &b&l1.0",
			"&b&lWarriors &f&l1&b&l.0",
			"&b&lWarriors 1&f&l.&b&l0",
			"&b&lWarriors 1.&f&l0",
			"&b&lWarriors 1.0",
			"&f&lWarriors 1.0",
			"&b&lWarriors 1.0",
			"&f&lWarriors 1.0",
	};
	
    public void updateName(final Player p) {
//        final RankedPlayer rp = new RankedPlayer(p.getUniqueId());
//        p.setPlayerListName(rp.getRank().getPrefix() + ChatColor.RESET + p.getName());
//        final EntityPlayer entityPlayer = ((CraftPlayer)p).getHandle();
//        entityPlayer.displayName = rp.getRank().getPrefix().trim() + " &r" + p.getName();
//        for (final Player a : Bukkit.getOnlinePlayers()) {
//            ((CraftPlayer)a).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutNamedEntitySpawn((EntityHuman)entityPlayer));
//        }
    }
    
    /*
     *  SCOREBOARD
     */
    public void setupSidebar(final Player p) {
    	if(!scoreboardCache.containsKey(p))
    		scoreboardCache.put(p, new CustomScoreboard(p, scoreboardTitles[0], Arrays.copyOfRange(scoreboardTitles, 1, scoreboardTitles.length)));
    	
    	scoreboardCache.get(p).init();
    }
    
    @EventHandler
    public void updateScoreboard(UpdateScoreboardEvent e) {
    	ClansPlayer cp = new ClansPlayer(e.getScoreboard().getBound());
    	Chunk standing = e.getScoreboard().getBound().getLocation().getChunk();
    	boolean hasClan = cp.getRealClan() != null;

    	String clan = hasClan ? cp.getRealClan().getName() : "None";
    	String online = hasClan ? cp.getRealClan().getOnlinePlayers(false).size() + "/" + cp.getRealClan().getPlayers(false).size() : "N/A";
    	String energy = hasClan ? cp.getRealClan().getEnergyString() : "N/A";
    	String gold = Integer.toString(cp.getGold());
    	
    	String territory = Main.getInstance().getClanFromTerritory(standing) == null ? ChatColor.GRAY.toString() + "Wilderness" : 
    		(!hasClan ? ClanRelations.NEUTRAL.getPlayerColor().toString() : cp.getRealClan().getClanRelation(Main.getInstance().getClanFromTerritory(standing)
    				.getUniqueId()).getPlayerColor().toString()) + Main.getInstance().getClanFromTerritory(standing).getName();
    	territory = cp.isInSafeZone() ? String.format("&f(%s&f) %s", cp.isInCombat() ? ChatColor.RED.toString() + "UNSAFE" :
    		ChatColor.AQUA.toString() + "SAFE", ChatColor.stripColor(territory)) : territory;
    	
    	String[] lines = {
    			"",
    			"&eClan &f" + clan,
    			"&eOnline &f" + online,
    			"&eGold &f" + gold,
    			"&eEnergy &f" + energy,
    			"",
    			(Main.getInstance().getClanFromTerritory(standing) != null && Main.getInstance().getClanFromTerritory(standing).isServerClan() ? "&f" : "") + territory,
    			"",
    			null,
    			null,
    			null,
    			null,
    			null,
    			null,
    			null
    	};
    
    	
    	/*
    	 * SIEGES
    	 */
    	if(hasClan && (cp.getRealClan().isBeingSieged() || cp.getRealClan().isSiegingOther())) {
    		String action, timeLeft, clanActing;
    		
    		if(cp.getRealClan().isBeingSieged()) {
    			action = "Sieged";
    			timeLeft = cp.getRealClan().getClansSiegingSelf().get(0).getRemainingString(System.currentTimeMillis());
    			clanActing = cp.getRealClan().getClansSiegingSelf().get(0).getClanSieging().getName();
    		} else {
    			action = "Sieging";
    			timeLeft = cp.getRealClan().getClansSiegedBySelf().get(0).getRemainingString(System.currentTimeMillis());
    			clanActing = cp.getRealClan().getClansSiegedBySelf().get(0).getClanSieged().getName();
    		}

    		lines[8] = "&d&l" + action + " " + String.format("&f(%s&f)", ChatColor.RED + clanActing);
			lines[9] = "&f" + timeLeft;
			lines[10] = "";
    	}
    	
    	e.getScoreboard().setLines(lines);
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
    	if(this.scoreboardCache.containsKey(e.getPlayer())) {
    		this.scoreboardCache.get(e.getPlayer()).stop();
    		this.scoreboardCache.remove(e.getPlayer());
    	}
    }
    
    // END

    
    
    /*
     * NAME TAGS
     */
    public void updateNameTagsForAll() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            for (Player ps : Bukkit.getOnlinePlayers()) {
                this.updateNameTags(p, ps);
                if(p != ps) this.updateNameTags(ps, p);
            }
        }
    }

    public void updateNameTags(Player player, Player playersToSee) {
        ClansPlayer cp = new ClansPlayer(player);
        ClansPlayer toSee = new ClansPlayer(playersToSee);
        
        Clan clan = cp.getRealClan();
        Clan otherclan = toSee.getRealClan();
        boolean clanless = clan == null;

        String clanprefix = "", clanname = "", nameprefix = ClanRelations.NEUTRAL.getPlayerColor().toString();
        
        
        if (!clanless){
            clanname = clan.getName() + " ";
            clanprefix = ClanRelations.NEUTRAL.getClanColor().toString();

            if(otherclan != null) {
            	ClanRelations r = clan.getClanRelation(otherclan.getUniqueId());
            	clanprefix = r.getClanColor().toString();
            	nameprefix = r.getPlayerColor().toString();
            }
        }
        
        if (toSee.hasFocus() && toSee.getFocus().equals(player)) {
        	// Is Focusing Him
        	clanprefix = UtilFocus.CLAN_FOCUS.toString();
        	nameprefix = UtilFocus.PLAYER_FOCUS.toString();
        }

        Nametag packet = new Nametag(player, clan == null ? "None" : clan.getName(), clanprefix + clanname + nameprefix);
        packet.send(playersToSee);
    }

    // END
    
    
    
    /*
     * TAB LIST
     */
    public void updateTabListForAll() {
    	for(Player p : Bukkit.getOnlinePlayers()) this.updateTab(p);
    }
    
    public void updateTab(Player p) {
    	// TODO: Display custom tablist
    }
}
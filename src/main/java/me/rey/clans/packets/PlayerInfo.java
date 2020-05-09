package me.rey.clans.packets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.rey.clans.clans.Clan;
import me.rey.clans.utils.NMSUtil;
import me.rey.core.Warriors;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import me.rey.clans.Main;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.utils.Text;

public class PlayerInfo {
	
	List<String> scoreboardTitles = new ArrayList<>();
	
	private String color(String text) {
		return Text.color(text);
	}
	
    public void updateName(final Player p) {
//        final RankedPlayer rp = new RankedPlayer(p.getUniqueId());
//        p.setPlayerListName(rp.getRank().getPrefix() + ChatColor.RESET + p.getName());
//        final EntityPlayer entityPlayer = ((CraftPlayer)p).getHandle();
//        entityPlayer.displayName = rp.getRank().getPrefix().trim() + " &r" + p.getName();
//        for (final Player a : Bukkit.getOnlinePlayers()) {
//            ((CraftPlayer)a).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutNamedEntitySpawn((EntityHuman)entityPlayer));
//        }
    }
    
    public void setupSidebar(final Player p) {
        final ClansPlayer rp = new ClansPlayer(p);
        boolean hasClan = rp.getRealClan() != null;
        
        final ScoreboardManager scoreboardManager = Bukkit.getServer().getScoreboardManager();
        final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("SB", "Dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        final Team clan = scoreboard.registerNewTeam("clan");
        clan.addEntry(this.color("&1"));
        clan.setSuffix("");
        clan.setPrefix(this.color("&e&lClan"));
        objective.getScore(this.color("&1")).setScore(15);
        
        final Team clanName = scoreboard.registerNewTeam("clanName");
        clanName.addEntry(this.color("&2"));
        clanName.setSuffix("");
        clanName.setPrefix(this.color(ClanRelations.SELF.getPlayerColor() + (!hasClan ? "&7Wilderness" : rp.getRealClan().getName())));
        objective.getScore(this.color("&2")).setScore(14);
        
        final Team bar1 = scoreboard.registerNewTeam("bar1");
        bar1.addEntry(this.color("&3"));
        bar1.setSuffix("");
        bar1.setPrefix("");
        objective.getScore(this.color("&3")).setScore(13);
        
        final Team energy = scoreboard.registerNewTeam("energy");
        energy.addEntry(this.color("&4"));
        energy.setSuffix("");
        energy.setPrefix(this.color("&e&lClan Energy"));
        objective.getScore(this.color("&4")).setScore(12);
        
        final Team energyCount = scoreboard.registerNewTeam("energyCount");
        energyCount.addEntry(this.color("&5"));
        energyCount.setSuffix("");
        energyCount.setPrefix(this.color("&a" + (!hasClan ? "N/A" : rp.getRealClan().getEnergyString())));
        objective.getScore(this.color("&5")).setScore(11);
        
        final Team bar2 = scoreboard.registerNewTeam("bar2");
        bar2.addEntry(this.color("&6"));
        bar2.setSuffix("");
        bar2.setPrefix("");
        objective.getScore(this.color("&6")).setScore(10);
        
        final Team gold = scoreboard.registerNewTeam("gold");
        gold.addEntry(this.color("&7"));
        gold.setSuffix("");
        gold.setPrefix(this.color("&e&lGold"));
        objective.getScore(this.color("&7")).setScore(9);
        
        final Team goldCount = scoreboard.registerNewTeam("goldCount");
        goldCount.addEntry(this.color("&8"));
        goldCount.setSuffix("");
        goldCount.setPrefix(this.color("&6" + rp.getGold()));
        objective.getScore(this.color("&8")).setScore(8);
        
        final Team bar3 = scoreboard.registerNewTeam("bar3");
        bar3.addEntry(this.color("&9"));
        bar3.setSuffix("");
        bar3.setPrefix("");
        objective.getScore(this.color("&9")).setScore(7);
        
        final Team territory = scoreboard.registerNewTeam("territory");
        territory.addEntry(this.color("&0"));
        territory.setSuffix("");
        territory.setPrefix(this.color("&e&lTerritory"));
        objective.getScore(this.color("&0")).setScore(6);
        
        final Team territoryStanding = scoreboard.registerNewTeam("terrStanding");
        territoryStanding.addEntry(this.color("&a"));
        territoryStanding.setSuffix("");
        String color = "" + ChatColor.GRAY;
        if(!hasClan && rp.getClanInTerritory() != null)
        	color = "" + ClanRelations.NEUTRAL.getPlayerColor();
        if(hasClan && rp.getClanInTerritory() != null)
        	color = "" + rp.getClan().getClanRelation(rp.getClanInTerritory().getUniqueId()).getPlayerColor();
        if(rp.isInSafeZone())
        	color = String.format(Text.color("(%s&f) "),  rp.isInCombat() ? "&bSAFE" : "&bSAFE");
        
        String name = color + (rp.getClanInTerritory() == null ? "Wilderness" : rp.getClanInTerritory().getName());
        if(name.length() > 16) {
        	name = name.substring(0, 16);
        }
        territoryStanding.setPrefix(this.color(name));
        objective.getScore(this.color("&a")).setScore(5);
        
//        final Team bar4 = scoreboard.registerNewTeam("bar4");
//        bar4.addEntry(this.color("&b"));
//        bar4.setSuffix("");
//        bar4.setPrefix("");
//        
//        objective.getScore(this.color("&b")).setScore(4);
//        final Team website = scoreboard.registerNewTeam("website");
//        website.addEntry(this.color("&c"));
//        website.setSuffix("");
//        website.setPrefix(this.color("&c&lWebsite"));
//        objective.getScore(this.color("&c")).setScore(3);
//        
//        final Team site = scoreboard.registerNewTeam("site");
//        site.addEntry(this.color("&d"));
//        site.setSuffix("");
//        site.setPrefix(this.color("www.mineplex.com"));
//        objective.getScore(this.color("&d")).setScore(2);
//        
//        final Team bar5 = scoreboard.registerNewTeam("bar5");
//        bar5.addEntry(this.color("&e"));
//        bar5.setSuffix("");
//        bar5.setPrefix(this.color("----------------"));
//        objective.getScore(this.color("&e")).setScore(1);
        
        if(scoreboardTitles.isEmpty()) {
        	scoreboardTitles.add(this.color("&6&lWarriors Map 1"));
        	scoreboardTitles.add(this.color("&f&lW&6&larriors Map 1"));
        	scoreboardTitles.add(this.color("&6&lW&f&la&6&lrriors Map 1"));
        	scoreboardTitles.add(this.color("&6&lWa&f&lr&6&lriors Map 1"));
        	scoreboardTitles.add(this.color("&6&lWar&f&lr&6&liors Map 1"));
        	scoreboardTitles.add(this.color("&6&lWarr&f&li&6&lors Map 1"));
        	scoreboardTitles.add(this.color("&6&lWarrio&f&lr&6&ls Map 1"));
        	scoreboardTitles.add(this.color("&6&lWarrior&f&ls &6&lMap 1"));
        	scoreboardTitles.add(this.color("&6&lWarriors &f&lM&6&lap 1"));
        	scoreboardTitles.add(this.color("&6&lWarriors M&f&la&6&lp 1"));
        	scoreboardTitles.add(this.color("&6&lWarriors Ma&f&lp&6&l 1"));
        	scoreboardTitles.add(this.color("&6&lWarriors Map &f&l1"));
        	scoreboardTitles.add(this.color("&6&lWarriors Map 1"));
        	scoreboardTitles.add(this.color("&f&lWarriors Map 1"));
        	scoreboardTitles.add(this.color("&6&lWarriors Map 1"));
        	scoreboardTitles.add(this.color("&f&lWarriors Map 1"));
        	scoreboardTitles.add(this.color("&6&lWarriors Map 1"));
        	scoreboardTitles.add(this.color("&f&lWarriors Map 1"));
        	scoreboardTitles.add(this.color("&6&lWarriors Map 1"));
        }
        
        new BukkitRunnable() {
        	
            int pos = 0;
            public void run() {
                if (pos >= scoreboardTitles.size())
                	pos = 0;
                
                objective.setDisplayName(scoreboardTitles.get(pos));
                pos++;
            }
            
        }.runTaskTimer(JavaPlugin.getPlugin(Main.class), 0, 2);
        
        final String title = this.color("&f&l");
        objective.setDisplayName(title);
        p.setScoreboard(scoreboard);
    }
    
    public void updateScoreboard(final Player player) {
        if(player.getScoreboard() == null)
        	setupSidebar(player);
        Scoreboard board = player.getScoreboard();
        
        final ClansPlayer rp = new ClansPlayer(player);
        boolean hasClan = rp.getRealClan() != null;
        
        final Team clanName = board.getTeam("clanName");
        final Team energyCount = board.getTeam("energyCount");
        final Team goldCount = board.getTeam("goldCount");
        final Team territoryStanding = board.getTeam("terrStanding");

        clanName.setPrefix(this.color(ClanRelations.SELF.getPlayerColor() + (!hasClan ? "&7None" : rp.getRealClan().getName())));
        energyCount.setPrefix(this.color("&a" + (!hasClan ? "N/A" : rp.getRealClan().getEnergyString())));
        goldCount.setPrefix(this.color("&6" + rp.getGold()));
        
        String color = "" + ChatColor.GRAY;
        if(!hasClan && rp.getClanInTerritory() != null)
        	color = "" + ClanRelations.NEUTRAL.getPlayerColor();
        if(hasClan && rp.getClanInTerritory() != null)
        	color = "" + rp.getClan().getClanRelation(rp.getClanInTerritory().getUniqueId()).getPlayerColor();
        if(rp.isInSafeZone())
        	color = String.format(Text.color("(%s&f) "),  rp.isInCombat() ? "&bSAFE" : "&bSAFE");
        
        String name = color + (rp.getClanInTerritory() == null ? "Wilderness" : rp.getClanInTerritory().getName());
        if(name.length() > 16) {
        	name = name.substring(0, 16);
        }
        territoryStanding.setPrefix(this.color(name));
    }

    public void updateNameTagsForAll() {
	    Bukkit.broadcastMessage("updating nametags...");
        for(Player p : Bukkit.getOnlinePlayers()) {
            for (Player ps : Bukkit.getOnlinePlayers()) {
                this.updateNameTags(p, ps);
                this.updateNameTags(ps, p);
            }
        }
    }

    public void updateNameTags(Player player, Player playersToSee) {
        ClansPlayer cp = new ClansPlayer(player);
        Clan clan = cp.getRealClan();
        boolean clanless = clan == null;


        net.minecraft.server.v1_8_R3.Scoreboard scoreboard = ((CraftScoreboardManager)Bukkit.getServer().getScoreboardManager()).getNewScoreboard().getHandle();
        ScoreboardTeam team = scoreboard.getTeam(clanless ? "None" : clan.getName());
        if (team == null) {
            team = scoreboard.createTeam(clanless ? "None" : clan.getName());
        }

        PacketPlayOutScoreboardTeam teamPacket = new PacketPlayOutScoreboardTeam(team, 0);
        PacketPlayOutScoreboardTeam teamColorPacket = new PacketPlayOutScoreboardTeam(team, 2);
        PacketPlayOutScoreboardTeam joinPacket = new PacketPlayOutScoreboardTeam(team, Arrays.asList(player.getName()), 3);
        ClansPlayer toSee = new ClansPlayer(playersToSee);

        Clan otherclan = toSee.getRealClan();

        String clanprefix = "";
        String clanname = "";

        String nameprefix = "";

        if(clanless) {
            nameprefix = ChatColor.YELLOW.toString();

        } else {
            clanname = clan.getName() + " ";

            if(otherclan != null) {
                if (clan.getUniqueId() == otherclan.getUniqueId()) {
                    clanprefix = ClanRelations.SELF.getClanColor().toString();
                    nameprefix = ChatColor.AQUA.toString();
                }

                if (clan.getClanRelation(otherclan.getUniqueId()) == ClanRelations.NEUTRAL) {
                    clanprefix = ClanRelations.NEUTRAL.getClanColor().toString();
                    nameprefix = ChatColor.YELLOW.toString();
                }

                if (clan.getClanRelation(otherclan.getUniqueId()) == ClanRelations.ALLY) {
                    clanprefix = ClanRelations.ALLY.getClanColor().toString();
                    nameprefix = ChatColor.GREEN.toString();
                }

                if (clan.getClanRelation(otherclan.getUniqueId()) == ClanRelations.ENEMY) {
                    clanprefix = ClanRelations.ENEMY.getClanColor().toString();
                    nameprefix = ChatColor.LIGHT_PURPLE.toString();
                }
            }

        }

        NMSUtil.getAndSetField(teamColorPacket.getClass(), "c", teamColorPacket, clanprefix + clanname + nameprefix);
        //NMSUtil.getAndSetField(teamColorPacket.getClass(), "d", teamColorPacket, "");
        ((CraftPlayer)toSee.getPlayer()).getHandle().playerConnection.sendPacket(teamPacket);
        ((CraftPlayer)toSee.getPlayer()).getHandle().playerConnection.sendPacket(teamColorPacket);
        ((CraftPlayer)toSee.getPlayer()).getHandle().playerConnection.sendPacket(joinPacket);

    }

}

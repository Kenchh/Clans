package me.rey.clans.sidebar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import me.rey.clans.Main;
import me.rey.core.utils.Text;

public class CustomScoreboard {
	
	private BukkitTask task;
	private Player bound;
	private Scoreboard sb;
	private Objective obj;
	private String[] titles, lines;
	private int currentTitle;
	
	public CustomScoreboard(Player bound, String title, String... extraTitles) {
		this.bound = bound; 
		this.lines = new String[15];
		this.titles = new String[] {title};
		this.titles[0] = Text.color(title);
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.sb = manager.getNewScoreboard();
        
        this.obj = sb.registerNewObjective("bar", "dummy");
        obj.setDisplayName(Text.color(title));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        for(int i = 0; i < 15; i++) sb.registerNewTeam("team" + (i+1));
        
        if(extraTitles != null && extraTitles.length > 0)
        	for(String s : extraTitles) this.addTitle(s);
        
        this.currentTitle = 1;
	}
	
	public Player getBound() {
		return bound;
	}
	
	public CustomScoreboard init() {
		this.bound.setScoreboard(sb);
		update();
		
		task = new BukkitRunnable() {
			@Override
			public void run() {
				if(bound == null || !bound.isOnline()) {
					this.cancel();
					return;
				}
				
				callEvent();
				
				currentTitle = currentTitle + 1 > getTitles().length ? 1 : currentTitle + 1;
				update();
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 2);
		return this;
	}
	
	public CustomScoreboard stop() {
		if(task != null)
			task.cancel();
		return this;
	}
	
	public String[] getTitles() {
		return this.titles;
	}
	
	public String[] getLines() {
		return this.lines;
	}
	
	public CustomScoreboard setTitles(String... titles) {
		for(int i = 0; i < titles.length; i++) titles[i] = titles[i] == null ? null : Text.color(titles[i]);
		this.titles = titles;
		return this;
	}
	
	public CustomScoreboard addTitle(String title) {
		String[] query = new String[titles.length + 1];
		for(int i = 0; i < titles.length; i++) query[i] = titles[i];
		query[query.length-1] = Text.color(title);
		this.titles = query;
		return this;
	}
	
	public CustomScoreboard addLine(String text) {
		if(lines.length >= 15) return this;
		lines[lines.length] = Text.color(text);
		return this;
	}
	
	public CustomScoreboard setLines(String[] lines) {
		for(int i = 0; i < lines.length; i++) lines[i] = lines[i] == null ? null : Text.color(lines[i]);
		this.lines = lines;
		return this;
	}
	
	private void update() {
		
		if(bound.getScoreboard() == null)
			bound.setScoreboard(this.sb);
		
		Scoreboard scoreboard = bound.getScoreboard();
		scoreboard.getObjective(DisplaySlot.SIDEBAR).setDisplayName(this.getTitles()[currentTitle-1]);
		
		int count = 0;
		for(int i = 0; i < this.getLines().length; i++) {
			if(this.getLines()[i] == null) {
				if(getEntryFromScore(obj, 15-i) != null) scoreboard.resetScores(getEntryFromScore(obj, 15-i));
				continue;
			}
			
			if(i > 10) System.out.println(i);
 			String line = this.getLines()[i];;
 			int score = 15-i;
			if(line.equals("")) {
				line = ChatColor.values()[count].toString();
				count++;
			}
			
			Team team = scoreboard.getTeam("team" + Math.min(15, i+1));
			
			String[] allChars = new String[3];
			allChars[1] = line;
			
			if(line.length() > 16) {
				allChars[0] = line.substring(0, 16);
				allChars[1] = line.substring(16, Math.min(32, line.length()));
				if(line.length() > 32) allChars[2] = line.substring(32, Math.min(48, line.length()));
			}
			
			team.setPrefix(allChars[0] == null ? "" : allChars[0]);
		    replaceScore(obj, team, score, allChars[1]);
		    team.setSuffix(allChars[2] == null ? "" : allChars[2]);
		    
		    obj.getScore(line).setScore(score);	
		}
	}
	
	private void callEvent() {
		UpdateScoreboardEvent event = new UpdateScoreboardEvent(this);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}
	
	private String getEntryFromScore(Objective o, int score) {
	    if(o == null) return null;
	    if(!hasScoreTaken(o, score)) return null;
	    for (String s : o.getScoreboard().getEntries()) {
	        if(o.getScore(s).getScore() == score) return o.getScore(s).getEntry();
	    }
	    return null;
	}

	private boolean hasScoreTaken(Objective o, int score) {
	    for (String s : o.getScoreboard().getEntries()) {
	        if(o.getScore(s).getScore() == score) return true;
	    }
	    return false;
	}

	private void replaceScore(Objective o, Team team, int score, String name) {
		name = team == null ? name : team.getPrefix() + name + team.getSuffix();
		
	    if(hasScoreTaken(o, score)) {
	    	Scoreboard s = o.getScoreboard();
	    	String found = getEntryFromScore(o, score);
	    	if(s.getEntryTeam(found) != null)
	    		found = s.getEntryTeam(found).getPrefix() + found + s.getEntryTeam(found).getSuffix();
	    	
	    	
	        if(found.equals(name))
	        	return;
	        else
	        	o.getScoreboard().resetScores(getEntryFromScore(o, score));
	    }
	    o.getScore(name).setScore(score);
	}

}

package me.rey.clans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.rey.clans.clans.Clan;
import me.rey.clans.clans.ClanRelations;
import me.rey.clans.clans.ClansPlayer;
import me.rey.core.events.customevents.combat.DamageEvent;

public class CombatBaseRelation implements Listener {
		
	@EventHandler
	public void onDamage(DamageEvent e) {
		ClansPlayer cp = new ClansPlayer(e.getDamager());
		if(!cp.hasClan() || !(e.getDamagee() instanceof Player)) return;
		
		ClansPlayer damaged = new ClansPlayer((Player) e.getDamagee());
		if(!damaged.hasClan()) return;
		
		Clan hitter = cp.getClan();
		Clan hit = damaged.getClan();
		
		ClanRelations relation = hitter.getClanRelation(hit.getUniqueId());
		if(!relation.equals(ClanRelations.ALLY) && !relation.equals(ClanRelations.SELF)) return;
		
		e.setCancelled(true);
		cp.sendMessageWithPrefix("Combat", "You cannot hit " + relation.getPlayerColor() + damaged.getPlayer().getName() + "&r.");
	}

}

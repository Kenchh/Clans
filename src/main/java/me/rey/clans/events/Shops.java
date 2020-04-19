package me.rey.clans.events;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.rey.clans.clans.ClansPlayer;
import me.rey.clans.packets.Title;
import me.rey.core.classes.abilities.AbilityType;
import me.rey.core.events.customevents.AbilityUseEvent;
import me.rey.core.events.customevents.CombatTimerEndEvent;
import me.rey.core.events.customevents.CombatTimerTickEvent;
import me.rey.core.events.customevents.DamageEvent;
import me.rey.core.utils.Text;

public class Shops implements Listener {
	
	Set<AbilityType> disallowedAbilities = new HashSet<>(Arrays.asList(AbilityType.SWORD, AbilityType.BOW, AbilityType.AXE));
	
	@EventHandler
	public void onCombatEnd(CombatTimerEndEvent e) {
		Player p = e.getPlayer();
		ClansPlayer cp = new ClansPlayer(p);
		
		if(cp.isInSafeZone()) {
			// PLAYER IS NOW SAFE
			new Title("", Text.color("&aSAFE"), 0, 20, 0).send(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onCombatTick(CombatTimerTickEvent e) {
		Player p = e.getPlayer();
		ClansPlayer cp = new ClansPlayer(p);
		
		if(cp.isInSafeZone() && cp.isInCombat()) {
			// PARTICLES
			Location particles = p.getEyeLocation().clone().add(0, 1.5, 0);
			p.getWorld().spigot().playEffect(particles, Effect.CRIT, 0, 0, 0.1F, 0.1F, 0.1F, 0F, 5, 100);
			
			// TITLE
			String seconds = String.format("%.1f", e.getTimer().getRemaining(System.currentTimeMillis()));
			new Title("", Text.color("&cUnsafe for &a" + seconds + " &cseconds"), 0, 5, 0).send(p);
		}
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onAbilityUse(AbilityUseEvent e) {
		if(!disallowedAbilities.contains(e.getAbility().getAbilityType())) return;
		
		ClansPlayer cp = new ClansPlayer(e.getPlayer());
		if(cp.isInSafeZone()) {
			e.setCancelled(true);
			cp.sendMessageWithPrefix("Error", String.format("You cannot use &a%s &rin a Safe Zone!", e.getAbility().getName()));
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onDamage(DamageEvent e) {
		if (!(e.getDamagee() instanceof Player))
			return;

		Player d = (Player) e.getDamagee();
		if (new ClansPlayer(d).isInSafeZone() && !new ClansPlayer(d).isInCombat()) {
			ClansPlayer cp = new ClansPlayer(e.getDamager());
			cp.sendMessageWithPrefix("Error", "You cannot hit players in a Safe Zone!");
			e.setCancelled(true);
		}
	}

}

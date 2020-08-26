package me.rey.clans.worldevents.queenabilities;

import java.util.Iterator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.reinforced.bosses.Boss;
import com.reinforced.bosses.Boss.BossAbility;

import me.rey.core.effects.ParticleEffect.ColoredParticle;
import me.rey.core.effects.repo.Silence;
import me.rey.core.utils.UtilBlock;
import me.rey.core.utils.UtilParticle;

public class Blackjack extends BossAbility {
	
	private final double radius = 30D;
	private final double silenceTime = 20D;

	public Blackjack(Boss boss) {
		boss.super(boss, "Blackjack");
	}

	@Override
	protected boolean run() {
		Iterator<Entity> toSilence = UtilBlock.getEntitiesInCircle(boss.getAlive().getLocation(), radius).iterator();
		
		while(toSilence.hasNext()) {
			Entity found = toSilence.next();
			
			if(!(found instanceof Player)) continue;
			
			Player ent = (Player) found;
			new Silence().apply(ent, silenceTime);
			
			ColoredParticle particle = new ColoredParticle(129, 236, 236);
			UtilParticle.makeParticlesBetween(boss.getAlive().getLocation(), ent.getEyeLocation(), particle, 0.3);
		}
		
		return true;
	}

}

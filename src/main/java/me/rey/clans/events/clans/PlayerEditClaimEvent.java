package me.rey.clans.events.clans;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.rey.clans.clans.Clan;
import me.rey.clans.gui.Gui.Item;

public class PlayerEditClaimEvent extends Event {

	private ClaimPermission permission;
	private Clan ownsTerritory;
	private Item hand;
	private Block block;
	private Player player;
	
	public PlayerEditClaimEvent(Clan ownsTerritory, Player issuer, ClaimPermission permission, Item blockInhand, Block toReplace) {
		
		this.player = issuer;
		this.ownsTerritory = ownsTerritory;
		this.permission = permission;
		this.hand = blockInhand;
		this.block = toReplace;
	}

	private static final HandlerList HANDLERS = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Block getBlockToReplace() {
		return block;
	}
	
	public Item getItemInHand() {
		return hand;
	}
	
	public Clan getTerritoryOwner() {
		return ownsTerritory;
	}
	
	public ClaimPermission getPermission() {
		return permission;
	}

	public void setPermission(ClaimPermission permission) {
		this.permission = permission;
	}
	
	public static enum ClaimPermission {
		ALLOW, DENY;
	}

}

package me.rey.clans.events.clans;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.rey.clans.clans.Clan;

public class PlayerEditClaimEvent extends Event {

	private ClaimPermission permission;
	private EditAction action;
	private Clan ownsTerritory;
	private ItemStack hand;
	private Block block;
	private Player player;
	
	public PlayerEditClaimEvent(Clan ownsTerritory, Player issuer, ClaimPermission permission, EditAction action, ItemStack blockInhand, Block toReplace) {
		
		this.action = action;
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
	
	public EditAction getAction() {
		return action;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Block getBlockToReplace() {
		return block;
	}
	
	public ItemStack getItemInHand() {
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
	
	public static enum EditAction {
		PLACE, BREAK;
	}

}

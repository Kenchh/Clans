package me.rey.clans.packets;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import me.rey.clans.utils.NMSUtil;
import me.rey.core.utils.Text;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

public class TabList extends Packets {

	private String[] header, footer;
	private int size;

	public TabList(TabSize rows, String[] header, String[] footer) {
		this.header = header;
		this.footer = footer;
		this.size = rows == TabSize.NONE ? -1 : rows.getSize();
	}

	public String[] getHeader() {
		return header;
	}

	public String[] getFooter() {
		return footer;
	}

	public TabList setHeader(String[] header) {
		this.header = header;
		return this;
	}

	public TabList setFooter(String[] footer) {
		this.footer = footer;
		return this;
	}
	
	public void clear(LivingEntity entity) {
		
	}

	@Override
	public void send(LivingEntity entity) {
		Player player = (Player) entity;

		for(int i = 0; i < 5; i++) {
			PacketPlayOutPlayerInfo packet2 = new PacketPlayOutPlayerInfo();		
			NMSUtil.getAndSetField(packet2.getClass(), "a", packet2, EnumPlayerInfoAction.ADD_PLAYER);
			
			List<PlayerInfoData> dataList = Lists.newArrayList();
			dataList.add(packet2.new PlayerInfoData(
					new GameProfile(UUID.randomUUID(), Text.color("&r")),
					0,
					EnumGamemode.SURVIVAL,
					ChatSerializer.a("{\"text\":\"" + "" + "\"}")
			));
			
			NMSUtil.getAndSetField(packet2.getClass(), "b", packet2, dataList);
	
			this.sendPacket(player, packet2);
		}
	}

	public enum TabSize {

		NONE(0), ONE(20), TWO(40), THREE(60), FOUR(80);

		private int size;

		TabSize(int size){
			this.size = size;
		}

		public int getSize() {
			return size;
		}

	}
}

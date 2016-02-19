package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataserver.player.OnlinePlayer.Setting;
import lombok.Getter;

public class PacketInChangePlayerSettings extends Packet{
	@Getter
	private UUID player;
	@Getter
	private Setting setting;
	@Getter
	private String value;
	
	@Override
	public void read(DataBuffer buffer) {
		player = buffer.readUUID();
		setting = Setting.values()[buffer.readByte()];
		value = buffer.readString();
	}
}

package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PacketOutChangePlayerSettings extends Packet{
	public static enum Setting {
		PREMIUM_LOGIN,
		PASSWORD,
		UUID;
	}
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
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeByte(setting.ordinal());
		buffer.writeString(value);
	}
}

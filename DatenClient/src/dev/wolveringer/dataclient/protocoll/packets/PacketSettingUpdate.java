package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutChangePlayerSettings.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PacketSettingUpdate extends Packet{
	private UUID player;
	private Setting setting;
	private String value;
	
	public void read(DataBuffer buffer) {
		player = buffer.readUUID();
		setting = Setting.values()[buffer.readInt()];
		value = buffer.readString();
	};
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeInt(setting.ordinal());
		buffer.writeString(value);
	}
}

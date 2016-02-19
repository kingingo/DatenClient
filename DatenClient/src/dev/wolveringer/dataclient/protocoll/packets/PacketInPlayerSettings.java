package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutChangePlayerSettings.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PacketInPlayerSettings extends Packet{
	@AllArgsConstructor
	@Getter
	public static class SettingValue {
		private Setting setting;
		private String value;
	}
	private UUID player;
	private SettingValue[] values;
	
	@Override
	public void read(DataBuffer buffer) {
		player = buffer.readUUID();
		values = new SettingValue[buffer.readByte()];
		for(int i = 0;i<values.length;i++)
			values[i] = new SettingValue(Setting.values()[buffer.readByte()],buffer.readString());
	}
}

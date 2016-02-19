package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataserver.player.OnlinePlayer.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PacketOutPlayerSettings extends Packet{
	@AllArgsConstructor
	@Getter
	public static class SettingValue {
		private Setting setting;
		private String value;
	}
	private UUID player;
	private SettingValue[] values;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeByte(values.length);
		for(SettingValue val : values){
			buffer.writeByte(val.setting.ordinal());
			buffer.writeString(val.value);
		}
	}
}

package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutChangePlayerSettings.Setting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PacketOutPlayerSettingsRequest extends Packet{
	@Getter
	private UUID player;
	@Getter
	private Setting[] settings;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeByte(settings.length);
		for(Setting s : settings)
			buffer.writeByte(s.ordinal());
	}
}

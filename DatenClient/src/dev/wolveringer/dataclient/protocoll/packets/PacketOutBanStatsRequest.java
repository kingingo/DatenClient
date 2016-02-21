package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PacketOutBanStatsRequest extends Packet{
	@Getter
	private UUID player;
	@Getter
	private String ip;
	@Getter
	private String name;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeString(ip);
		buffer.writeString(name);
	}
}

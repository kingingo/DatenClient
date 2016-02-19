package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PacketInBanStatsRequest extends Packet{
	@Getter
	private UUID player;
	@Getter
	private String ip;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeString(ip);
	}
}

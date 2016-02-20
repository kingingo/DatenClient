package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketOutUUIDRequest extends Packet {
	private String[] players;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeByte(players.length);
		for(int i = 0;i<players.length;i++)
			buffer.writeString(players[i]);
	}
}

package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PacketInPlayerServer extends Packet{
	@Getter
	private UUID player;
	@Getter
	private String server;
	
	@Override
	public void read(DataBuffer buffer) {
		player = buffer.readUUID();
		server = buffer.readString();
	}
}

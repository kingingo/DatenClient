package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class PacketOutServerSwitch extends Packet {
	@Getter
	private UUID player;
	@Getter
	private String server;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeString(server);
	}
}

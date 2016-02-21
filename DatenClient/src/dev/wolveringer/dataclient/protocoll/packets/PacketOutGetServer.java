package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PacketOutGetServer extends Packet{
	@Getter
	private UUID player;

	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
	}
}

package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PacketOutNameRequest extends Packet{
	@Getter
	private UUID[] uuids;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeByte(uuids.length);
		for(UUID u : uuids)
			buffer.writeUUID(u);
	}
}

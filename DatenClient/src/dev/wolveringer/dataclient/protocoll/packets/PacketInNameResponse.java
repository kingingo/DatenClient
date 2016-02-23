package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PacketInNameResponse extends Packet{
	@Getter
	private PacketInUUIDResponse.UUIDKey[] response;
	
	@Override
	public void read(DataBuffer buffer) {
		response = new PacketInUUIDResponse.UUIDKey[buffer.readByte()];
		for (int i = 0; i < response.length; i++) {
			response[i] = new PacketInUUIDResponse.UUIDKey(buffer.readString(), buffer.readUUID());
		}
	}

	public String[] getNames() {
		String[] out = new String[response.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = response[i].getName();
		}
		return out;
	}

	public UUID[] getUUIDs() {
		UUID[] out = new UUID[response.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = response[i].getUuid();
		}
		return out;
	}
}

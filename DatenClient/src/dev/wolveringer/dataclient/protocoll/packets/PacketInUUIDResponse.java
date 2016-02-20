package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PacketInUUIDResponse extends Packet{
	@Getter
	@AllArgsConstructor
	public static class UUIDKey {
		private String name;
		private UUID uuid;
	}
	
	@Getter
	private UUIDKey[] uuids = null;
	@Getter
	private String[] names = null;
	@Override
	public void read(DataBuffer buffer) {
		uuids = new UUIDKey[buffer.readByte()];
		names = new String[uuids.length];
		for(int i = 0;i<uuids.length;i++){
			uuids[i] = new UUIDKey(names[i] = buffer.readString(), buffer.readUUID());
		}
	}
}

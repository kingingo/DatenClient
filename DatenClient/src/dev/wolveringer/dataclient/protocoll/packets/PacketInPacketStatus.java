package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class PacketInPacketStatus extends Packet {
	@AllArgsConstructor
	public static class Error {
		@Getter
		private int id;
		@Getter
		private String message;
	}

	@Getter
	private UUID packetId;
	@Getter
	@NonNull
	private Error[] errors;

	public PacketInPacketStatus(Packet receved, Error... errors) {
		this.packetId = receved.getPacketUUID();
		this.errors = errors;
	}

	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(packetId);
		if (errors != null) {
			buffer.writeByte(errors.length);
			for (Error r : errors) {
				buffer.writeInt(r.id);
				buffer.writeString(r.message);
			}
		} else
			buffer.writeByte(0);
	}
	
	@Override
	public void read(DataBuffer buffer) {
		packetId = buffer.readUUID();
		errors = new Error[buffer.readByte()];
		for(int i = 0;i<errors.length;i++){
			errors[i] = new Error(buffer.readInt(), buffer.readString());
		}
	}
}

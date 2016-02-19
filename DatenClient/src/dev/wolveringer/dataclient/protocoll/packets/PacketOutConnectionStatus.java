package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class PacketOutConnectionStatus extends Packet{
	public static enum Status {
		CONNECTED,
		DISCONNECTED;
		private Status() {
			// TODO Auto-generated constructor stub
		}
	}
	@Getter
	private UUID player;
	@Getter
	private Status status;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeByte(status.ordinal());
	}
}

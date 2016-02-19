package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.client.connection.ClientType;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PacketOutHandschakeStart extends Packet{
	@Getter
	private String host;
	@Getter
	private String name;
	@Getter
	private byte[] password;
	@Getter
	private ClientType type;

	@Override
	public void write(DataBuffer buffer) {
		buffer.writeString(host);
		buffer.writeString(name);
		buffer.writeByte(password.length);
		buffer.writeBytes(password);
		buffer.writeByte(type.ordinal());
	}
}

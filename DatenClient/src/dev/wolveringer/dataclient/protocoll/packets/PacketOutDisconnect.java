package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PacketOutDisconnect extends Packet{
	private String reson = null;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeString(reson);
	}
}

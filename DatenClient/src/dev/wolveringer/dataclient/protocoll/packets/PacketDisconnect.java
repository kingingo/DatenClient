package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PacketDisconnect extends Packet{
	private String reson = null;
	
	@Override
	public void read(DataBuffer buffer) {
		reson = buffer.readString();
	}
	
	public String getReson() {
		return reson;
	}
}

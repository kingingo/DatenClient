package dev.wolveringer.dataclient.protocoll.packets;

import lombok.Getter;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketPingPong extends Packet{
	private long time;
	
	@Override
	public void read(DataBuffer buffer) {
		time = buffer.readLong();
	}
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeLong(time);
	}
}

package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataclient.protocoll.packets.PacketInServerStatus.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PacketOutServerStatusRequest extends Packet{
	private Action action;
	private String value;
	private boolean player;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeByte(action.ordinal());
		buffer.writeString(value);
		buffer.writeBoolean(player);
	}
}

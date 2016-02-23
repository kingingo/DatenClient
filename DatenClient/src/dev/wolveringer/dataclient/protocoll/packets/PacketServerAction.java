package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PacketServerAction extends Packet{
	@AllArgsConstructor
	@Getter
	public static class PlayerAction {
		private UUID player;
		private Action action;
		private String value;
	}
	public static enum Action {
		SEND,
		KICK;
	}
	
	@Getter
	private PlayerAction[] actions;
	
	@Override
	public void read(DataBuffer buffer) {
		actions = new PlayerAction[buffer.readByte()];
		for (int i = 0; i < actions.length; i++) {
			actions[i] = new PlayerAction(buffer.readUUID(), Action.values()[buffer.readByte()], buffer.readString());
		}
	}
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeByte(actions.length);
		for(PlayerAction a : actions){
			buffer.writeUUID(a.player);
			buffer.writeByte(a.getAction().ordinal());
			buffer.writeString(a.getValue());
		}
	}
}

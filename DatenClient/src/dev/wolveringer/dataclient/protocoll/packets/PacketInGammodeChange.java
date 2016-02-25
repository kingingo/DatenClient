package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PacketInGammodeChange extends Packet{
	private GameType game;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeByte(game.ordinal());
	}
	
	@Override
	public void read(DataBuffer buffer) {
		game = GameType.values()[buffer.readByte()];
	}
}

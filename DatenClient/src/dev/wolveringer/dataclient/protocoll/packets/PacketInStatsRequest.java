package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataserver.gamestats.Game;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PacketInStatsRequest extends Packet{
	private UUID player;
	private Game game;
	
	@Override
	public void read(DataBuffer buffer) {
		player = buffer.readUUID();
		game = Game.values()[buffer.readByte()];
	}
}

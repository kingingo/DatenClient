package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.gamestats.Game;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class PacketOutStatsRequest extends Packet{
	private UUID player;
	private Game game;

	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		buffer.writeByte(game.ordinal());
	}
}

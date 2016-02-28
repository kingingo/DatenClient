package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.gamestats.StatsKey;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PacketOutTopTenRequest extends Packet{
	private GameType game;
	private StatsKey condition;
	
	@Override
	public void read(DataBuffer buffer) {
		this.game = GameType.values()[buffer.readByte()];
		this.condition = StatsKey.values()[buffer.readByte()];
	}
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeByte(game.ordinal());
		buffer.writeByte(condition.ordinal());
	}
}

package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.gamestats.Statistic;
import dev.wolveringer.dataclient.gamestats.StatsKey;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PacketInStats extends Packet{
	private UUID player;
	private GameType game;
	private Statistic[] stats;
	
	@Override
	public void read(DataBuffer buffer) {
		player = buffer.readUUID();
		game = GameType.values()[buffer.readByte()];
		stats = new Statistic[buffer.readByte()];
		
		for(int i = 0;i<stats.length;i++){
			StatsKey key = StatsKey.values()[buffer.readByte()];
			int value = buffer.readByte();
			Object val = null;
			switch (value) {
			case 0:
				val = buffer.readInt();
				break;
			case 1:
				val = buffer.readDouble();
				break;
			case 2:
				val = buffer.readString();
				break;
			default:
				System.out.println("Wron stats id: "+value);
				break;
			}
			stats[i] = new Statistic(key, val);
		}
		
	}
}

package dev.wolveringer.dataclient.protocoll.packets;

import java.util.UUID;

import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.gamestats.StatsKey;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class PacketOutStatsEdit extends Packet {
	@AllArgsConstructor
	@Getter
	public static class EditStats {
		private GameType game;
		private Action action;
		private StatsKey key;
		private Object value;
	}

	public static enum Action {
		ADD, REMOVE, SET;
		private Action() {
		}
	}

	@Getter
	private UUID player;
	@Getter
	private EditStats[] changes;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeUUID(player);
		
		buffer.writeByte(changes.length);
		for(EditStats stats : changes){
			buffer.writeByte(stats.game.ordinal());
			buffer.writeByte(stats.action.ordinal());
			buffer.writeByte(stats.key.ordinal());
			buffer.writeByte(stats.key.getClassId());
			switch (stats.key.getClassId()) { // Value Type
			case 0:
				buffer.writeInt((int) stats.value);
				break;
			case 1:
				buffer.writeDouble((double) stats.value);
				break;
			case 2:
				buffer.writeString((String) stats.value);
				break;
			default:
				System.out.println("Wron stats id: -1");
				break;
			}
		}
	}
}

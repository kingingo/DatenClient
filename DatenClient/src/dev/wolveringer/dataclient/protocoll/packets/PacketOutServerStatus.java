package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class PacketOutServerStatus extends Packet{
	private int bitmask = 0; //TODO minimize data
	private int players;
	private int maxPlayers;
	private String mots; //Message of the server :D equals <-> Message of the day (MOTD)
	private GameType typ;
	private boolean lobby;
	
	@Override
	public void read(DataBuffer buffer) {
		bitmask = buffer.readByte();
		players = buffer.readInt();
		maxPlayers = buffer.readInt();
		mots = buffer.readString();
		typ = GameType.values()[buffer.readByte()];
		lobby = buffer.readBoolean();
	}
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeByte(bitmask);
		buffer.writeInt(players);
		buffer.writeInt(maxPlayers);
		buffer.writeString(mots);
		buffer.writeByte(typ.ordinal());
		buffer.writeBoolean(lobby);
	}
}

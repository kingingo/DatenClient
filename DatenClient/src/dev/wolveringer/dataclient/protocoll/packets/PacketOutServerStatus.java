package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.gamestats.Game;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PacketOutServerStatus extends Packet{
	private int bitmask = 0; //TODO minimize data
	private int players;
	private int maxPlayers;
	private String mots; //Message of the server :D equals <-> Message of the day (MOTD)
	private Game typ;
	private boolean lobby;
	
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeInt(players);
		buffer.writeInt(maxPlayers);
		buffer.writeString(mots);
		buffer.writeByte(typ.ordinal());
		buffer.writeBoolean(lobby);
	}
}

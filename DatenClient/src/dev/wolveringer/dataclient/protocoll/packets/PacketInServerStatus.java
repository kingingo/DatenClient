package dev.wolveringer.dataclient.protocoll.packets;

import java.util.ArrayList;
import java.util.List;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketInServerStatus extends Packet{
	public static enum Action {
		SERVER,
		BUNGEECORD,
		GENERAL;
	}
	
	private Action action;
	private String value;
	private int player;
	private int maxPlayers;
	private List<String> players;

	@Override
	public void read(DataBuffer buffer) {
		this.action = Action.values()[buffer.readByte()];
		this.value = buffer.readString();
		this.player = buffer.readInt();
		this.maxPlayers = buffer.readInt();
		if(buffer.readBoolean()){
			players = new ArrayList<>();
			int length = buffer.readInt();
			for(int i = 0;i<length;i++)
				players.add(buffer.readString());
		}
	}
}

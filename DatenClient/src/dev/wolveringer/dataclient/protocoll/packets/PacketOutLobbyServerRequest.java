package dev.wolveringer.dataclient.protocoll.packets;

import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PacketOutLobbyServerRequest extends Packet{
	@AllArgsConstructor
	@Getter
	public static class GameRequest {
		private GameType game;
		private int maxServers;
	}
	
	private GameRequest[] request;
	
	@Override
	public void read(DataBuffer buffer) {
		this.request = new GameRequest[buffer.readByte()];
		for (int i = 0; i < request.length; i++) {
			request[i] = new GameRequest(GameType.values()[buffer.readByte()], buffer.readInt());
		}
	}
	@Override
	public void write(DataBuffer buffer) {
		buffer.writeByte(request.length);
		for(GameRequest r : request){
			buffer.writeByte(r.game.ordinal());
			buffer.writeInt(r.getMaxServers());
		}
	}
}

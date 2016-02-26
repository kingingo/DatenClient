package dev.wolveringer.client.connection;

import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerStatus.GameState;

public interface ServerInformations {
	public int getPlayers();
	public int getMaxPlayers();
	public boolean isVisiable();
	public String getMOTS();
	public GameType getType();
	public GameState getServerState();
	public String getServerId();
}

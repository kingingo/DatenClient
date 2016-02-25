package dev.wolveringer.client.connection;

import dev.wolveringer.dataclient.gamestats.GameType;

public interface ServerInformations {
	public int getPlayers();
	public int getMaxPlayers();
	public boolean isIngame();
	public String getMOTS();
	public GameType getType();
}

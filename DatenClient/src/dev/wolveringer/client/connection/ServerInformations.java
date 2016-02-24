package dev.wolveringer.client.connection;

import dev.wolveringer.dataclient.gamestats.Game;

public interface ServerInformations {
	public int getPlayers();
	public int getMaxPlayers();
	public boolean isIngame();
	public String getMOTS();
	public Game getType();
}

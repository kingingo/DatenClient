package dev.wolveringer.client.external;

import dev.wolveringer.dataclient.gamestats.GameType;

public interface ServerActionListener extends ActionListener{
	public void setGamemode(GameType game);
}

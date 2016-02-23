package dev.wolveringer.client.external;

import dev.wolveringer.dataclient.gamestats.Game;

public interface ServerActionListener extends ActionListener{
	public void setGamemode(Game game);
}

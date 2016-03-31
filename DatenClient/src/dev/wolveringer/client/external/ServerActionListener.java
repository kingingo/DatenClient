package dev.wolveringer.client.external;

import dev.wolveringer.dataserver.gamestats.GameType;

public interface ServerActionListener extends ActionListener{
	public void setGamemode(GameType game,String subtype);
}

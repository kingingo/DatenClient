package dev.wolveringer.client.external;

import java.util.UUID;

public interface BungeeCordActionListener extends ActionListener{
	public void sendPlayer(int player,String server);
}

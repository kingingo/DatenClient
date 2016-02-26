package dev.wolveringer.client.connection;

import dev.wolveringer.dataclient.gamestats.GameType;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerStatus;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerStatus.GameState;

public interface ServerInformations {
	public PacketOutServerStatus getStatus();
}

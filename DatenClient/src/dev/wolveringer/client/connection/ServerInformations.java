package dev.wolveringer.client.connection;

import dev.wolveringer.dataserver.protocoll.packets.PacketInServerStatus;

public interface ServerInformations {
	public PacketInServerStatus getStatus();
}

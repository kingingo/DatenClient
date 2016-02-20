package dev.wolveringer.client.connection;

import dev.wolveringer.dataclient.protocoll.packets.Packet;

public interface PacketListener {
	public void handle(Packet packet);
}

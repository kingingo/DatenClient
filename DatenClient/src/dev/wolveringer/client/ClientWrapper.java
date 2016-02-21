package dev.wolveringer.client;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.futures.StatusResponseFuture;
import dev.wolveringer.client.futures.UUIDFuture;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutUUIDRequest;

public class ClientWrapper {
	protected Client handle;
	
	public ClientWrapper(Client handle) {
		this.handle = handle;
	}
	
	public UUIDFuture getUUID(String...players){
		Packet packet = new PacketOutUUIDRequest(players);
		UUIDFuture future = new UUIDFuture(handle, packet, players);
		handle.writePacket(packet);
		return future;
	}
	public StatusResponseFuture writePacket(Packet packet){
		StatusResponseFuture f = new StatusResponseFuture(handle, packet.getPacketUUID());
		handle.writePacket(packet);
		return f;
	}
}

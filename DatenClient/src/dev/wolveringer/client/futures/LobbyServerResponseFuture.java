package dev.wolveringer.client.futures;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketInLobbyServerRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutLobbyServer;

public class LobbyServerResponseFuture extends PacketResponseFuture<PacketOutLobbyServer>{
	private Packet request;
	
	public LobbyServerResponseFuture(Client owner,PacketInLobbyServerRequest packet) {
		super(owner, packet);
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutLobbyServer)
			done((PacketOutLobbyServer) packet);
	}
}

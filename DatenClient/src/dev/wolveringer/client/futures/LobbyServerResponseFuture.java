package dev.wolveringer.client.futures;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInLobbyServer;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutLobbyServerRequest;

public class LobbyServerResponseFuture extends PacketResponseFuture<PacketInLobbyServer>{
	private Packet request;
	
	public LobbyServerResponseFuture(Client owner,PacketOutLobbyServerRequest packet) {
		super(owner, packet);
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInLobbyServer)
			done((PacketInLobbyServer) packet);
	}
}

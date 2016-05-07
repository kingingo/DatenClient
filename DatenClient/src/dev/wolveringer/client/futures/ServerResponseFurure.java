package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPlayerServer;

public class ServerResponseFurure extends PacketResponseFuture<String>{
	private int player;
	
	public ServerResponseFurure(Client client,Packet handeling,int player) {
		super(client,handeling);
		this.player = player;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutPlayerServer && ((PacketOutPlayerServer) packet).getPlayer() == player)
			done(((PacketOutPlayerServer) packet).getServer());
	}
}

package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPlayerServer;

public class ServerResponseFurure extends PacketResponseFuture<String>{
	private UUID player;
	
	public ServerResponseFurure(Client client,Packet handeling,UUID player) {
		super(client,handeling);
		this.player = player;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInPlayerServer && ((PacketInPlayerServer) packet).getPlayer().equals(player))
			done(((PacketInPlayerServer) packet).getServer());
	}
}

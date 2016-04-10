package dev.wolveringer.client.futures;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketPlayerIdRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketPlayerIdResponse;

public class PlayerIdResponseFuture extends PacketResponseFuture<int[]>{
	private PacketPlayerIdRequest request;
	
	public PlayerIdResponseFuture(Client client, PacketPlayerIdRequest handeling) {
		super(client, handeling);
		this.request = handeling;
	}

	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketPlayerIdResponse && ((PacketPlayerIdResponse) packet).getRequestUUID().equals(request.getPacketUUID()))
			done(((PacketPlayerIdResponse) packet).getIds());
	}

}

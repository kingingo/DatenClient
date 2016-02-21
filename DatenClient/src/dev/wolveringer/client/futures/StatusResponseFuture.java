package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPacketStatus;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPacketStatus.Error;

public class StatusResponseFuture extends PacketResponseFuture<Error[]>{
	private UUID packet;
	
	public StatusResponseFuture(Client client,UUID packet) {
		super(client,null);
		this.packet = packet;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInPacketStatus && ((PacketInPacketStatus) packet).getPacketId().equals(this.packet)){
			done(((PacketInPacketStatus) packet).getErrors());
		}
	}
}

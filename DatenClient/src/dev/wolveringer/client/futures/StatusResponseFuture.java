package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;

public class StatusResponseFuture extends PacketResponseFuture<dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus.Error[]>{
	private UUID packet;
	
	public StatusResponseFuture(Client client,UUID packet) {
		super(client,null);
		this.packet = packet;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutPacketStatus && ((PacketOutPacketStatus) packet).getPacketId().equals(this.packet)){
			done(((PacketOutPacketStatus) packet).getErrors());
		}
	}
}

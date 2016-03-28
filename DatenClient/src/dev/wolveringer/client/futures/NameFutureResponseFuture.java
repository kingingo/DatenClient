package dev.wolveringer.client.futures;

import java.util.Arrays;
import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutNameResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutUUIDResponse.UUIDKey;

public class NameFutureResponseFuture extends PacketResponseFuture<UUIDKey[]>{
	private UUID[] uuids;
	
	public NameFutureResponseFuture(Client client,Packet handeling,UUID[] names) {
		super(client,handeling);
		this.uuids = names;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutNameResponse && Arrays.equals(((PacketOutNameResponse) packet).getUUIDs(), uuids)){
			done(((PacketOutNameResponse)packet).getResponse());
		}
	}
}

package dev.wolveringer.client.futures;

import java.util.Arrays;
import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInNameResponse;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse.UUIDKey;

public class NameFutureResponseFuture extends PacketResponseFuture<UUIDKey[]>{
	private UUID[] uuids;
	
	public NameFutureResponseFuture(Client client,Packet handeling,UUID[] names) {
		super(client,handeling);
		this.uuids = names;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInNameResponse && Arrays.equals(((PacketInNameResponse) packet).getUUIDs(), uuids)){
			done(((PacketInNameResponse)packet).getResponse());
		}
	}
}

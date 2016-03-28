package dev.wolveringer.client.futures;

import java.util.Arrays;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutUUIDResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutUUIDResponse.UUIDKey;

public class UUIDFuture extends PacketResponseFuture<UUIDKey[]>{
	private String[] names;
	
	public UUIDFuture(Client client,Packet handeling,String[] names) {
		super(client,handeling);
		this.names = names;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutUUIDResponse && Arrays.equals(((PacketOutUUIDResponse) packet).getNames(), names)){
			done(((PacketOutUUIDResponse)packet).getUuids());
		}
	}
}

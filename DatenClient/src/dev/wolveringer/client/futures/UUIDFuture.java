package dev.wolveringer.client.futures;

import java.util.Arrays;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse.UUIDKey;

public class UUIDFuture extends PacketResponseFuture<UUIDKey[]>{
	private String[] names;
	
	public UUIDFuture(Client client,Packet handeling,String[] names) {
		super(client,handeling);
		this.names = names;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInUUIDResponse && Arrays.equals(((PacketInUUIDResponse) packet).getNames(), names)){
			done(((PacketInUUIDResponse)packet).getUuids());
		}
	}
}

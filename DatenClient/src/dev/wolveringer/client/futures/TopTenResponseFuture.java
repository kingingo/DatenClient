package dev.wolveringer.client.futures;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInTopTen;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutTopTenRequest;

public class TopTenResponseFuture extends PacketResponseFuture<PacketInTopTen>{
	private PacketOutTopTenRequest packet;
	
	public TopTenResponseFuture(Client handle,PacketOutTopTenRequest packet) {
		super(handle, packet);
		this.packet = packet;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInTopTen && ((PacketInTopTen) packet).getCondition() == this.packet.getCondition() && ((PacketInTopTen) packet).getGame() == this.packet.getGame())
			done((PacketInTopTen) packet);
	}
}

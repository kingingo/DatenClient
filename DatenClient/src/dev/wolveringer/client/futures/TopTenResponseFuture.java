package dev.wolveringer.client.futures;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketInTopTenRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutTopTen;

public class TopTenResponseFuture extends PacketResponseFuture<PacketOutTopTen>{
	private PacketInTopTenRequest packet;
	
	public TopTenResponseFuture(Client handle,PacketInTopTenRequest packet) {
		super(handle, packet);
		this.packet = packet;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutTopTen){
			if(((PacketOutTopTen) packet).getCondition() == this.packet.getCondition() && ((PacketOutTopTen) packet).getGame() == this.packet.getGame()){
				done((PacketOutTopTen) packet);
			}
		}
	}
}

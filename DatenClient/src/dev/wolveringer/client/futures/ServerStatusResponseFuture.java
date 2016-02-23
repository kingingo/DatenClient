package dev.wolveringer.client.futures;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInServerStatus;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerStatusRequest;

public class ServerStatusResponseFuture extends PacketResponseFuture<PacketInServerStatus>{
	private PacketOutServerStatusRequest r;
	
	public ServerStatusResponseFuture(Client c,PacketOutServerStatusRequest r) {
		super(c,r);
		this.r = r;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInServerStatus && ((PacketInServerStatus) packet).getAction() == r.getAction() && ((PacketInServerStatus) packet).getValue() == r.getValue()){
			done((PacketInServerStatus) packet);
		}
	}
}

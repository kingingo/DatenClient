package dev.wolveringer.client.futures;

import java.util.Arrays;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketInServerStatusRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus;

public class ServerStatusResponseFuture extends PacketResponseFuture<PacketOutServerStatus>{
	private PacketInServerStatusRequest r;
	
	public ServerStatusResponseFuture(Client c,PacketInServerStatusRequest r) {
		super(c,r);
		this.r = r;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutServerStatus && ((PacketOutServerStatus) packet).getAction() == r.getAction() && ((PacketOutServerStatus) packet).getValue() == r.getValue() && Arrays.equals(((PacketOutServerStatus) packet).getGames(), r.getGames())){
			done((PacketOutServerStatus) packet);
		}
	}
}

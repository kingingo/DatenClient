package dev.wolveringer.client.futures;

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
		if(packet instanceof PacketOutServerStatus && ((PacketOutServerStatus) packet).getAction() == r.getAction() && equals(((PacketOutServerStatus) packet).getValue(), r.getValue())/* && Arrays.equals(((PacketOutServerStatus) packet).getGames(), r.getGames())*/){
			done((PacketOutServerStatus) packet);
		}
	}
	
	 private boolean equals(String str1, String str2) {
	        return str1 == null ? str2 == null : str1.equals(str2);
	    }
}

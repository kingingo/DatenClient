package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.ban.BanEntity;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketInBanStatsRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutBanStats;

public class BanStatsResponseFuture extends PacketResponseFuture<BanEntity> {
	private UUID packet;
	
	public BanStatsResponseFuture(Client client,PacketInBanStatsRequest packet) {
		super(client,packet);
		this.packet = packet.getPacketUUID();
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutBanStats && ((PacketOutBanStats) packet).getRequest().equals(this.packet)){
			done(((PacketOutBanStats) packet).getE());
		}
	}
}

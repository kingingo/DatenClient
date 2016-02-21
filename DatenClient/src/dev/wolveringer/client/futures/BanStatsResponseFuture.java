package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInBanStats;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutBanStatsRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketInBanStats.BanEntity;

public class BanStatsResponseFuture extends PacketResponseFuture<BanEntity> {
	private UUID packet;
	
	public BanStatsResponseFuture(Client client,PacketOutBanStatsRequest packet) {
		super(client,packet);
		this.packet = packet.getPacketUUID();
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInBanStats && ((PacketInBanStats) packet).getRequest().equals(this.packet)){
			done(((PacketInBanStats) packet).getE());
		}
	}
}

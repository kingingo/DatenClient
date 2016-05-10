package dev.wolveringer.client.futures;

import dev.wolveringer.booster.BoosterType;
import dev.wolveringer.booster.NetworkBooster;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketBoosterStatusResponse;

public class BoosterResposeFuture extends PacketResponseFuture<NetworkBooster>{
	private int playerId;
	private BoosterType type;
	
	public BoosterResposeFuture(Client client, Packet handeling,int playerId, BoosterType type) {
		super(client, handeling);
		this.playerId = playerId;
		this.type = type;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketBoosterStatusResponse){
			if(((PacketBoosterStatusResponse)packet).getPlayerId() == playerId || playerId == -1)
				if(((PacketBoosterStatusResponse) packet).getType() == type)
					done(new NetworkBooster(((PacketBoosterStatusResponse) packet).getStart(), ((PacketBoosterStatusResponse) packet).getTime(), ((PacketBoosterStatusResponse) packet).getPlayerId(), ((PacketBoosterStatusResponse) packet).getType(), ((PacketBoosterStatusResponse) packet).getStart() > 0));
		}
	}
	
}

package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildActionResponse;

public class GildCreateReponseFuture extends PacketResponseFuture<UUID>{
	private int playerId;
	public GildCreateReponseFuture(Client client, PacketGildAction handeling) {
		super(client, handeling);
		this.playerId = handeling.getPlayerId();
	}

	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketGildActionResponse)
			if(((PacketGildActionResponse) packet).getPlayerId() == playerId)
				done(((PacketGildActionResponse) packet).getUuid());
	}

}

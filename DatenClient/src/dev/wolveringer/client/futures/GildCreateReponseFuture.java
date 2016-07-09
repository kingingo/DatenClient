package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildCreate;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildCreateResponse;

public class GildCreateReponseFuture extends PacketResponseFuture<UUID>{
	private int playerId;
	public GildCreateReponseFuture(Client client, PacketGildCreate handeling) {
		super(client, handeling);
		this.playerId = handeling.getPlayerId();
	}

	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketGildCreateResponse)
			if(((PacketGildCreateResponse) packet).getPlayerId() == playerId)
				done(((PacketGildCreateResponse) packet).getUuid());
	}

}

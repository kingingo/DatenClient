package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinData;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinData.SkinResponse;
import dev.wolveringer.skin.Skin;

public class SkinResponseFuture extends PacketResponseFuture<SkinResponse[]> {
	private UUID skinRequest = null;

	public SkinResponseFuture(Client client, Packet request, UUID requestUUID) {
		super(client, request);
		this.skinRequest = requestUUID;
	}

	@Override
	public void handlePacket(Packet packet) {
		if (packet instanceof PacketSkinData) {
			PacketSkinData data = (PacketSkinData) packet;
			if (data.getRequestUUID().equals(skinRequest))
				done(data.getReponse());
		}
	}

}

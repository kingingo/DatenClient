package dev.wolveringer.client.futures;

import java.util.Arrays;
import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.debug.Debugger;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinData;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinData.SkinResponse;

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
			Debugger.debug("skin data: " + data.getRequestUUID()+" - "+skinRequest);
			if (data.getRequestUUID().equals(skinRequest)){
				done(data.getReponse());
			}
		}
	}

}

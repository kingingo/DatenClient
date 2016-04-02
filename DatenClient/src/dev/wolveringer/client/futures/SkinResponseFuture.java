package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinData;
import dev.wolveringer.skin.Skin;

public class SkinResponseFuture extends PacketResponseFuture<Skin> {
	private String skinName = null;
	private UUID skinUUID = null;

	public SkinResponseFuture(Client client, Packet request, String skinName) {
		super(client, request);
		this.skinName = skinName;
	}

	public SkinResponseFuture(Client client, Packet request, UUID skinUUID) {
		super(client, request);
		this.skinUUID = skinUUID;
	}

	@Override
	public void handlePacket(Packet packet) {
		if (packet instanceof PacketSkinData) {
			PacketSkinData data = (PacketSkinData) packet;
			if (data.getSkin().getProfileName().equalsIgnoreCase(skinName) || data.getSkin().getUUID() == skinUUID)
				done(data.getSkin());
		}
	}

}

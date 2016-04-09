package dev.wolveringer.client.futures;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketLanguageRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketLanguageResponse;

public class LanguageUpdateFuture<String> extends PacketResponseFuture<String>{
	private PacketLanguageRequest request;
	public LanguageUpdateFuture(Client client, PacketLanguageRequest handeling) {
		super(client, handeling);
		this.request = handeling;
	}

	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketLanguageResponse){
			if(request.getType() == ((PacketLanguageResponse)packet).getType())
				done((String) ((PacketLanguageResponse)packet).getFileContains());
		}
	}

}

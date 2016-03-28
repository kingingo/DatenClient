package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPlayerSettings;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPlayerSettings.SettingValue;

public class SettingsResponseFuture extends PacketResponseFuture<SettingValue[]>{
	private UUID player;
	
	public SettingsResponseFuture(Client client,Packet handeling,UUID player) {
		super(client,handeling);
		this.player = player;
	}
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutPlayerSettings && ((PacketOutPlayerSettings) packet).getPlayer().equals(player))
			done(((PacketOutPlayerSettings) packet).getValues());
	}
}

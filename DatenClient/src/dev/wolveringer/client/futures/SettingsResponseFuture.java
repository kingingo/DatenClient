package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPlayerSettings;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPlayerSettings.SettingValue;

public class SettingsResponseFuture extends PacketResponseFuture<SettingValue[]>{
	private UUID player;
	
	public SettingsResponseFuture(Client client,Packet handeling,UUID player) {
		super(client,handeling);
		this.player = player;
	}
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInPlayerSettings && ((PacketInPlayerSettings) packet).getPlayer().equals(player))
			done(((PacketInPlayerSettings) packet).getValues());
	}
}

package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutStats;
import dev.wolveringer.gamestats.Statistic;

public class StatsResponseFuture extends PacketResponseFuture<Statistic[]>{
	private UUID player;
	private GameType game;
	
	public StatsResponseFuture(Client client,Packet handeling,UUID player,GameType game) {
		super(client,handeling);
		this.player = player;
		this.game = game;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketOutStats && ((PacketOutStats) packet).getPlayer().equals(player) && ((PacketOutStats) packet).getGame().ordinal() == game.ordinal())
			done(((PacketOutStats) packet).getStats());
	}
}

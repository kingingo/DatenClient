package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInStats;
import dev.wolveringer.dataserver.gamestats.Game;
import dev.wolveringer.dataserver.gamestats.Statistic;

public class StatsResponseFuture extends PacketResponseFuture<Statistic[]>{
	private UUID player;
	private Game game;
	
	public StatsResponseFuture(Client client,Packet handeling,UUID player,Game game) {
		super(client,handeling);
		this.player = player;
		this.game = game;
	}
	
	@Override
	public void handlePacket(Packet packet) {
		if(packet instanceof PacketInStats && ((PacketInStats) packet).getPlayer().equals(player) && ((PacketInStats) packet).getGame().ordinal() == game.ordinal())
			done(((PacketInStats) packet).getStats());
	}
}

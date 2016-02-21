package dev.wolveringer.client;

import java.util.HashMap;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.futures.StatusResponseFuture;
import dev.wolveringer.client.futures.UUIDFuture;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutUUIDRequest;

public class ClientWrapper {
	protected Client handle;
	private HashMap<String, LoadedPlayer> players = new HashMap<>();
	
	public ClientWrapper(Client handle) {
		this.handle = handle;
	}
	
	public UUIDFuture getUUID(String...players){
		Packet packet = new PacketOutUUIDRequest(players);
		UUIDFuture future = new UUIDFuture(handle, packet, players);
		handle.writePacket(packet);
		return future;
	}
	public StatusResponseFuture writePacket(Packet packet){
		StatusResponseFuture f = new StatusResponseFuture(handle, packet.getPacketUUID());
		handle.writePacket(packet);
		return f;
	}
	
	public LoadedPlayer getPlayer(String name){
		if(players.containsKey(name))
			return players.get(name);
		LoadedPlayer player = new LoadedPlayer(this,name);
		players.put(name, player);
		return player;
	}
}

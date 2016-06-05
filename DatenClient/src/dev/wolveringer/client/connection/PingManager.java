package dev.wolveringer.client.connection;

import dev.wolveringer.dataserver.protocoll.packets.PacketPing;
import dev.wolveringer.dataserver.protocoll.packets.PacketPong;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PingManager {
	private long lastPingPacket = -1;
	private int toServerPing = -1;
	private int toClientPing = -1;
	private int ping = -1;
	private final Client client;
	
	public void ping(){
		if(lastPingPacket != -1)
			return;
		lastPingPacket = System.currentTimeMillis();
		client.writePacket(new PacketPing(System.currentTimeMillis()));
	}
	
	public void handlePong(PacketPong pong){
		ping = (int) (System.currentTimeMillis() - lastPingPacket);
		toServerPing = (int) (pong.getTime()-lastPingPacket);
		toClientPing = (int) (System.currentTimeMillis()-pong.getTime());
		lastPingPacket = -1;
	}
	
	public int getPing(){
		if(ping == -1 && lastPingPacket != -1)
			return (int) (System.currentTimeMillis()-lastPingPacket);
		return ping;
	}
	
	public int getCurrentPing(){
		if(lastPingPacket == -1)
			return getPing();
		return (int) (System.currentTimeMillis()-lastPingPacket);
	}
	
	public int getPingToServer(){
		return toServerPing;
	}
	public int getPingToClient(){
		return toClientPing;
	}
}

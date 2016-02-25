package dev.wolveringer.client.external;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;

public interface ActionListener {
	public void sendMessage(UUID player,String message);
	public void brotcast(String permission,String message);
	public void kickPlayer(UUID player,String message);
	
	public void disconnected();
	public void connected();
	
	public void serverMessage(String channel,DataBuffer buffer);
}

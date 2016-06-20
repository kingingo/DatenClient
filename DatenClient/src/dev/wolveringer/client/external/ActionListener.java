package dev.wolveringer.client.external;

import java.util.UUID;

import dev.wolveringer.client.connection.State;
import dev.wolveringer.dataserver.player.Setting;
import dev.wolveringer.dataserver.protocoll.DataBuffer;

public interface ActionListener {
	public void sendMessage(int player,String message);
	public void broadcast(String permission,String message);
	public void kickPlayer(int player,String message);
	
	public void error(State state,Exception e);
	public void disconnected();
	public void connected();
	
	public void serverMessage(String channel,DataBuffer buffer);
	
	public void settingUpdate(UUID player,Setting setting,String value);
	
	public void restart(String kickMessage);
	public void stop(String kickMessage);
}

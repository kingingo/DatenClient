package dev.wolveringer.client.external;

import java.util.UUID;

import dev.wolveringer.dataserver.player.Setting;
import dev.wolveringer.dataserver.protocoll.DataBuffer;

public interface ActionListener {
	public void sendMessage(UUID player,String message);
	public void brotcast(String permission,String message);
	public void kickPlayer(int player,String message);
	
	public void disconnected();
	public void connected();
	
	public void serverMessage(String channel,DataBuffer buffer);
	
	public void settingUpdate(UUID player,Setting setting,String value);
	
	public void restart(String kickMessage);
}

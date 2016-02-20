package dev.wolveringer.client;

import java.util.UUID;

import dev.wolveringer.client.futures.SettingsResponseFuture;
import dev.wolveringer.client.futures.StatsResponseFuture;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPlayerSettings.SettingValue;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutStatsRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse.UUIDKey;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutChangePlayerSettings;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutConnectionStatus;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutChangePlayerSettings.Setting;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutConnectionStatus.Status;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutPlayerSettingsRequest;
import dev.wolveringer.dataserver.gamestats.Game;

public abstract class LoadedPlayer {
	
	private UUID uuid;
	private String name;
	private ClientWrapper handle;
	
	public LoadedPlayer(ClientWrapper client,String name) {
		this.name = name;
		this.handle = client;
		System.out.println("Connected");
		client.writePacket(new PacketOutConnectionStatus(name, Status.CONNECTED)).getSync();
		loadUUID();
	}
	
	protected void loadUUID(){
		UUIDKey[] keys = handle.getUUID(name).getSync();
		if(keys == null || keys.length == 0)
			return;
		uuid = keys[0].getUuid();
	}
	public String getName(){
		return name;
	}
	public UUID getUUID(){
		if(uuid == null){
			loadUUID();
			if(uuid== null)
				throw new RuntimeException("UUID not found");
		}
		return uuid;
	}
	public void disconnect(){
		handle.writePacket(new PacketOutConnectionStatus(name, Status.DISCONNECTED)).getSync();
	}
	public StatsResponseFuture getStats(Game game){
		Packet packet = new PacketOutStatsRequest(getUUID(), game);
		StatsResponseFuture future = new StatsResponseFuture(handle.handle, getUUID(), game);
		handle.handle.writePacket(packet);
		return future;
	}
	public SettingsResponseFuture getSettings(Setting...settings){
		Packet packet = new PacketOutPlayerSettingsRequest(getUUID(), settings);
		SettingsResponseFuture future = new SettingsResponseFuture(handle.handle, getUUID());
		handle.handle.writePacket(packet);
		return future;
	}
	
	public String getPassword(){
		SettingValue[] response = getSettings(Setting.PASSWORD).getSyncSave();
		if(response.length == 1 && response[0].getSetting() == Setting.PASSWORD)
			return response[0].getValue();
		return null;
	}
	public void setPassword(String password){
		PacketOutChangePlayerSettings packet = new PacketOutChangePlayerSettings(getUUID(), Setting.PASSWORD, password);
		handle.writePacket(packet).getSync();
	}
	public boolean isPremium(){
		SettingValue[] response = getSettings(Setting.PREMIUM_LOGIN).getSyncSave();
		if(response.length == 1 && response[0].getSetting() == Setting.PREMIUM_LOGIN)
			return Boolean.valueOf(response[0].getValue());
		return false;
	}
	public void setPremium(boolean active){
		PacketOutChangePlayerSettings packet = new PacketOutChangePlayerSettings(getUUID(), Setting.PREMIUM_LOGIN, active+"");
		handle.writePacket(packet).getSync();
		loadUUID();
	}
}

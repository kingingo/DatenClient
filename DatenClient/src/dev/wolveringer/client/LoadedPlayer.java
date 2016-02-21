package dev.wolveringer.client;

import java.util.UUID;

import dev.wolveringer.client.futures.ServerResponseFurure;
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
import dev.wolveringer.dataclient.protocoll.packets.PacketOutGetServer;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutPlayerSettingsRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerSwitch;
import dev.wolveringer.dataserver.gamestats.Game;

public abstract class LoadedPlayer {

	private UUID uuid;
	private String name;
	private ClientWrapper handle;

	private boolean loaded;

	public LoadedPlayer(ClientWrapper client, String name) {
		this.name = name;
		this.handle = client;

	}

	protected void loadUUID() {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		UUIDKey[] keys = handle.getUUID(name).getSync();
		if (keys == null || keys.length == 0)
			return;
		uuid = keys[0].getUuid();
	}

	public String getName() {
		return name;
	}

	public UUID getUUID() {
		if (uuid == null) {
			loadUUID();
			if (uuid == null)
				throw new RuntimeException("UUID not found");
		}
		return uuid;
	}

	public void disconnect() {
		unload();
	}

	public void load() { //TODO check
		handle.writePacket(new PacketOutConnectionStatus(name, Status.CONNECTED)).getSync(); //Load player
		loaded = true;
		System.out.println("Player loaded");
		loadUUID();
	}

	public void unload() { //TODO check
		loaded = false;
		handle.writePacket(new PacketOutConnectionStatus(name, Status.DISCONNECTED)).getSync(); //Unload player
	}

	public StatsResponseFuture getStats(Game game) {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		Packet packet = new PacketOutStatsRequest(getUUID(), game);
		StatsResponseFuture future = new StatsResponseFuture(handle.handle, packet, getUUID(), game);
		handle.handle.writePacket(packet);
		return future;
	}

	public SettingsResponseFuture getSettings(Setting... settings) {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		Packet packet = new PacketOutPlayerSettingsRequest(getUUID(), settings);
		SettingsResponseFuture future = new SettingsResponseFuture(handle.handle, packet, getUUID());
		handle.handle.writePacket(packet);
		return future;
	}

	public String getPasswordSync() {
		SettingValue[] response = getSettings(Setting.PASSWORD).getSyncSave();
		if (response.length == 1 && response[0].getSetting() == Setting.PASSWORD)
			return response[0].getValue();
		return null;
	}

	public void setPasswordSync(String password) {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		PacketOutChangePlayerSettings packet = new PacketOutChangePlayerSettings(getUUID(), Setting.PASSWORD, password);
		handle.writePacket(packet).getSync();
	}

	public boolean isPremiumSync() {
		SettingValue[] response = getSettings(Setting.PREMIUM_LOGIN).getSyncSave();
		if (response.length == 1 && response[0].getSetting() == Setting.PREMIUM_LOGIN)
			return Boolean.valueOf(response[0].getValue());
		return false;
	}

	public void setPremiumSync(boolean active) {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		PacketOutChangePlayerSettings packet = new PacketOutChangePlayerSettings(getUUID(), Setting.PREMIUM_LOGIN, active + "");
		handle.writePacket(packet).getSync();
		loadUUID();
	}

	public void setServerSync(String server) {
		handle.writePacket(new PacketOutServerSwitch(getUUID(), server)).getSync();
	}

	public ServerResponseFurure getServer() {
		PacketOutGetServer p = new PacketOutGetServer(uuid);
		ServerResponseFurure f = new ServerResponseFurure(handle.handle, p, uuid);
		handle.handle.writePacket(p);
		return f;
	}
}

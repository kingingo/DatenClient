package dev.wolveringer.client;

import java.util.UUID;

import dev.wolveringer.client.futures.BanStatsResponseFuture;
import dev.wolveringer.client.futures.PacketResponseFuture;
import dev.wolveringer.client.futures.ServerResponseFurure;
import dev.wolveringer.client.futures.SettingsResponseFuture;
import dev.wolveringer.client.futures.StatsResponseFuture;
import dev.wolveringer.client.futures.StatusResponseFuture;
import dev.wolveringer.dataclient.gamestats.Game;
import dev.wolveringer.dataclient.gamestats.Statistic;
import dev.wolveringer.dataclient.gamestats.StatsKey;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPlayerSettings.SettingValue;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutStatsRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPacketStatus.Error;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse.UUIDKey;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutBanPlayer;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutBanStatsRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutChangePlayerSettings;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutConnectionStatus;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutChangePlayerSettings.Setting;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutConnectionStatus.Status;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutStatsEdit.Action;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutStatsEdit.EditStats;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutGetServer;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutPlayerSettingsRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerSwitch;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutStatsEdit;

public class LoadedPlayer {

	private UUID uuid;
	private String name;
	private ClientWrapper handle;

	private boolean loaded;

	protected LoadedPlayer(ClientWrapper client, String name) {
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
	
	public int getCoinsSync() {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		Packet packet = new PacketOutStatsRequest(getUUID(), Game.Money);
		StatsResponseFuture future = new StatsResponseFuture(handle.handle, packet, getUUID(), Game.Money);
		handle.handle.writePacket(packet);
		for(Statistic s : future.getSync())
			if(s.getStatsKey() == StatsKey.COINS)
				return s.asInt();
		return -1;
	}
	
	public PacketResponseFuture<Error[]> changeCoins(Action action,int coins){
		return setStats(new EditStats(Game.Money, action, StatsKey.COINS, coins));
	}

	public int getGemsSync() {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		Packet packet = new PacketOutStatsRequest(getUUID(), Game.Money);
		StatsResponseFuture future = new StatsResponseFuture(handle.handle, packet, getUUID(), Game.Money);
		handle.handle.writePacket(packet);
		for(Statistic s : future.getSync())
			if(s.getStatsKey() == StatsKey.GEMS)
				return s.asInt();
		return -1;
	}

	public PacketResponseFuture<Error[]> changeGems(Action action,int coins){
		return setStats(new EditStats(Game.Money, action, StatsKey.GEMS, coins));
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
		PacketOutGetServer p = new PacketOutGetServer(getUUID());
		ServerResponseFurure f = new ServerResponseFurure(handle.handle, p, uuid);
		handle.handle.writePacket(p);
		return f;
	}
	public StatusResponseFuture setStats(EditStats... changes){
		return handle.writePacket(new PacketOutStatsEdit(getUUID(), changes));
	}
	public BanStatsResponseFuture getBanStats(String ip){
		PacketOutBanStatsRequest p = new PacketOutBanStatsRequest(getUUID(),ip, name);
		BanStatsResponseFuture f = new BanStatsResponseFuture(handle.handle, p);
		handle.handle.writePacket(p);
		return f;
	}
	public StatusResponseFuture banPlayer(String curruntIp,String banner,String bannerIp,UUID bannerUUID,int level,long end,String reson){
		return handle.writePacket(new PacketOutBanPlayer(name, curruntIp, getUUID() + "", banner, bannerIp, bannerUUID+"", end, level, reson));
	}
}

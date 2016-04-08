package dev.wolveringer.client;

import java.util.Arrays;
import java.util.UUID;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import dev.wolveringer.client.futures.BanStatsResponseFuture;
import dev.wolveringer.client.futures.BaseProgressFuture;
import dev.wolveringer.client.futures.FutureResponseTransformer;
import dev.wolveringer.client.futures.ServerResponseFurure;
import dev.wolveringer.client.futures.SettingsResponseFuture;
import dev.wolveringer.client.futures.SkinResponseFuture;
import dev.wolveringer.client.futures.StatsResponseFuture;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import dev.wolveringer.dataserver.player.Setting;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketInBanPlayer;
import dev.wolveringer.dataserver.protocoll.packets.PacketInBanStatsRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInChangePlayerSettings;
import dev.wolveringer.dataserver.protocoll.packets.PacketInConnectionStatus;
import dev.wolveringer.dataserver.protocoll.packets.PacketInConnectionStatus.Status;
import dev.wolveringer.dataserver.protocoll.packets.PacketInGetServer;
import dev.wolveringer.dataserver.protocoll.packets.PacketInPlayerSettingsRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInServerSwitch;
import dev.wolveringer.dataserver.protocoll.packets.PacketInStatsEdit;
import dev.wolveringer.dataserver.protocoll.packets.PacketInStatsEdit.Action;
import dev.wolveringer.dataserver.protocoll.packets.PacketInStatsEdit.EditStats;
import dev.wolveringer.dataserver.protocoll.packets.PacketInStatsRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPlayerSettings.SettingValue;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutUUIDResponse.UUIDKey;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinData.SkinResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinRequest.SkinRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinRequest.Type;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinSet;
import dev.wolveringer.gamestats.Statistic;
import dev.wolveringer.skin.Skin;
import dev.wolveringer.skin.SteveSkin;

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
		if(loaded)
			return;
		handle.writePacket(new PacketInConnectionStatus(name, Status.CONNECTED)).getSync(); //Load player
		loaded = true;
		loadUUID();
	}

	public boolean isLoaded() {
		return loaded;
	}
	
	public void unload() { //TODO check
		if(!loaded)
			return;
		loaded = false;
		handle.writePacket(new PacketInConnectionStatus(name, Status.DISCONNECTED)).getSync(); //Unload player
	}

	public StatsResponseFuture getStats(GameType game) {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		Packet packet = new PacketInStatsRequest(getUUID(), game);
		StatsResponseFuture future = new StatsResponseFuture(handle.handle, packet, getUUID(), game);
		handle.handle.writePacket(packet);
		return future;
	}
	
	public int getCoinsSync() {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		Packet packet = new PacketInStatsRequest(getUUID(), GameType.Money);
		StatsResponseFuture future = new StatsResponseFuture(handle.handle, packet, getUUID(), GameType.Money);
		handle.handle.writePacket(packet);
		for(Statistic s : future.getSync())
			if(s.getStatsKey() == StatsKey.COINS)
				return s.asInt();
		return -1;
	}
	
	public ProgressFuture<PacketOutPacketStatus.Error[]> changeCoins(Action action,int coins){
		return setStats(new EditStats(GameType.Money, action, StatsKey.COINS, coins));
	}

	public int getGemsSync() {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		Packet packet = new PacketInStatsRequest(getUUID(), GameType.Money);
		StatsResponseFuture future = new StatsResponseFuture(handle.handle, packet, getUUID(), GameType.Money);
		handle.handle.writePacket(packet);
		for(Statistic s : future.getSync())
			if(s.getStatsKey() == StatsKey.GEMS)
				return s.asInt();
		return -1;
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> changeGems(Action action,int coins){
		return setStats(new EditStats(GameType.Money, action, StatsKey.GEMS, coins));
	}
	
	public LanguageType getLanguageSync(){
		SettingValue[] response = getSettings(Setting.LANGUAGE).getSyncSave();
		if (response != null && response.length == 1 && response[0].getSetting() == Setting.LANGUAGE)
			return LanguageType.get(response[0].getValue());
		return LanguageType.ENGLISH;
	}
	
	public SettingsResponseFuture getSettings(Setting... settings) {
		if (!loaded)
			throw new RuntimeException("Player not loaded. Invoke load() at first");
		Packet packet = new PacketInPlayerSettingsRequest(getUUID(), settings);
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
		PacketInChangePlayerSettings packet = new PacketInChangePlayerSettings(getUUID(), Setting.PASSWORD, password);
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
		UUID old = getUUID();
		PacketInChangePlayerSettings packet = new PacketInChangePlayerSettings(old, Setting.PREMIUM_LOGIN, active + "");
		handle.writePacket(packet).getSync();
		loadUUID();
		handle.changeUUID(this, old, getUUID());
	}

	public void setServerSync(String server) {
		handle.writePacket(new PacketInServerSwitch(getUUID(), server)).getSync();
	}

	public ServerResponseFurure getServer() {
		PacketInGetServer p = new PacketInGetServer(getUUID());
		ServerResponseFurure f = new ServerResponseFurure(handle.handle, p, uuid);
		handle.handle.writePacket(p);
		return f;
	}
	public ProgressFuture<PacketOutPacketStatus.Error[]> setStats(EditStats... changes){
		Iterable<PacketInStatsEdit.EditStats> statis = Iterables.filter(Arrays.asList(changes),new Predicate<PacketInStatsEdit.EditStats>() {
		    @Override
		    public boolean apply(PacketInStatsEdit.EditStats arg0) {
		        if(arg0==null)
		            return false;
		        if(arg0.getAction() == null || arg0.getGame() == null || arg0.getKey() == null || arg0.getValue() == null)
		            return false;
		        return true;
		    }
		});
		return handle.writePacket(new PacketInStatsEdit(getUUID(), FluentIterable.from(statis).toArray(EditStats.class)));
	}
	public BanStatsResponseFuture getBanStats(String ip){
		PacketInBanStatsRequest p = new PacketInBanStatsRequest(getUUID(),ip, name);
		BanStatsResponseFuture f = new BanStatsResponseFuture(handle.handle, p);
		handle.handle.writePacket(p);
		return f;
	}
	public ProgressFuture<PacketOutPacketStatus.Error[]> banPlayer(String curruntIp,String banner,String bannerIp,UUID bannerUUID,int level,long end,String reson){
		return handle.writePacket(new PacketInBanPlayer(name, curruntIp, getUUID() + "", banner, bannerIp, bannerUUID+"", end, level, reson));
	}
	public ProgressFuture<PacketOutPacketStatus.Error[]> kickPlayer(String reson){
		return handle.kickPlayer(getUUID(), reson);
	}
	public ProgressFuture<Skin> getOwnSkin() {
		UUID uuid = UUID.randomUUID();
		PacketSkinRequest r = new PacketSkinRequest(uuid, new PacketSkinRequest.SkinRequest[]{new PacketSkinRequest.SkinRequest(Type.FROM_PLAYER, null, getUUID())});
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[],Skin>(new SkinResponseFuture(handle.handle, r, uuid)) {
			@Override
			public Skin transform(SkinResponse[] obj) {
				if(obj.length > 0){
					if(obj[0] == null){
						return new SteveSkin();
					}
					return obj[0].getSkin();
				}
				return null;
			}
		};
	}
	public ProgressFuture<PacketOutPacketStatus.Error[]> setOwnSkin(Skin skin){
		if(skin == null)
			return handle.writePacket(new PacketSkinSet(getUUID()));
		else
			return handle.writePacket(new PacketSkinSet(getUUID(), skin));
	}
}

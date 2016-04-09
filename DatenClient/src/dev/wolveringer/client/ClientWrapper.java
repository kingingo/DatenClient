package dev.wolveringer.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.ClientType;
import dev.wolveringer.client.futures.FutureResponseTransformer;
import dev.wolveringer.client.futures.LanguageUpdateFuture;
import dev.wolveringer.client.futures.LobbyServerResponseFuture;
import dev.wolveringer.client.futures.NameFutureResponseFuture;
import dev.wolveringer.client.futures.ServerStatusResponseFuture;
import dev.wolveringer.client.futures.SkinResponseFuture;
import dev.wolveringer.client.futures.TopTenResponseFuture;
import dev.wolveringer.client.futures.UUIDFuture;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import dev.wolveringer.dataserver.player.LanguageType;
import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketChatMessage;
import dev.wolveringer.dataserver.protocoll.packets.PacketChatMessage.TargetType;
import dev.wolveringer.dataserver.protocoll.packets.PacketForward;
import dev.wolveringer.dataserver.protocoll.packets.PacketInLobbyServerRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInLobbyServerRequest.GameRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInNameRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInServerStatusRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInTopTenRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInUUIDRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketLanguageRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutLobbyServer;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus.Action;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutTopTen;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutUUIDResponse.UUIDKey;
import dev.wolveringer.skin.Skin;
import dev.wolveringer.skin.SteveSkin;
import dev.wolveringer.dataserver.protocoll.packets.PacketServerAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketServerMessage;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinData.SkinResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinRequest.Type;

public class ClientWrapper {
	protected Client handle;
	private HashMap<String, LoadedPlayer> players = new HashMap<>();
	private HashMap<UUID, LoadedPlayer> uuidPlayers = new HashMap<>();

	public ClientWrapper(Client handle) {
		this.handle = handle;
	}

	public UUIDFuture getUUID(String... players) {
		Packet packet = new PacketInUUIDRequest(players);
		UUIDFuture future = new UUIDFuture(handle, packet, players);
		handle.writePacket(packet);
		return future;
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> writePacket(Packet packet) {
		return handle.writePacket(packet);
	}

	public LoadedPlayer getPlayer(String name) {
		if (players.containsKey(name))
			return players.get(name);
		LoadedPlayer player = new LoadedPlayer(this, name);
		players.put(name, player);
		return player;
	}

	public LoadedPlayer getPlayer(UUID uuid) {
		if (uuidPlayers.containsKey(uuid))
			return uuidPlayers.get(uuid);
		String name = getName(uuid);
		if (name == null)
			throw new NullPointerException("Player not found");
		return getPlayer(name);
	}

	public LoadedPlayer getPlayerAndLoad(String name) {
		LoadedPlayer player = getPlayer(name);
		if (!player.isLoaded())
			player.load();
		if (!players.containsKey(player.getName()))
			players.put(player.getName(), player);
		if (!uuidPlayers.containsKey(player.getUUID()))
			uuidPlayers.put(player.getUUID(), player);
		return player;
	}

	public LoadedPlayer getPlayerAndLoad(UUID name) {
		LoadedPlayer player = getPlayer(name);
		if (!player.isLoaded())
			player.load();
		if (!uuidPlayers.containsKey(player.getUUID()))
			uuidPlayers.put(player.getUUID(), player);
		if (!players.containsKey(player.getName()))
			players.put(player.getName(), player);
		return player;
	}

	public void clearCacheForPlayer(LoadedPlayer player) {
		if (player == null)
			return;
		for (String s : new ArrayList<>(players.keySet()))
			if (players.get(s).equals(player))
				players.remove(s);
		for (UUID s : new ArrayList<>(uuidPlayers.keySet()))
			if (uuidPlayers.get(s).equals(player))
				uuidPlayers.remove(s);
	}

	private String getName(UUID uuid) {
		PacketInNameRequest r = new PacketInNameRequest(new UUID[] { uuid });
		NameFutureResponseFuture f = new NameFutureResponseFuture(handle, r, new UUID[] { uuid });
		handle.writePacket(r);
		UUIDKey[] key = f.getSync();
		if (key.length == 1)
			return key[0].getName();
		return null;
	}

	public ServerStatusResponseFuture getServerStatus(dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus.Action action, String server) {
		return getServerStatus(action, server, false);
	}

	public ServerStatusResponseFuture getServerStatus(dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus.Action action, String server, boolean player) {
		PacketInServerStatusRequest p;
		if(action == Action.GAMETYPE)
			throw new RuntimeException("GAMETYPE isnt an spectial server");
		writePacket(p = new PacketInServerStatusRequest(action, server, player,null));
		return new ServerStatusResponseFuture(handle, p);
	}

	public ServerStatusResponseFuture getGameTypeServerStatus(GameType[] types, boolean player) {
		PacketInServerStatusRequest p;
		writePacket(p = new PacketInServerStatusRequest(dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus.Action.GAMETYPE, null, player,types));
		return new ServerStatusResponseFuture(handle, p);
	}
	
	public ProgressFuture<PacketOutPacketStatus.Error[]> sendMessage(UUID player, String message) {
		PacketChatMessage p = new PacketChatMessage(message, new PacketChatMessage.Target[] { new PacketChatMessage.Target(TargetType.PLAYER, null, player.toString()) });
		return writePacket(p);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> brotcastMessage(String permission, String message) {
		PacketChatMessage p = new PacketChatMessage(message, new PacketChatMessage.Target[] { new PacketChatMessage.Target(TargetType.BROTCAST, permission, message) });
		return writePacket(p);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> kickPlayer(UUID player, String reson) {
		PacketServerAction action = new PacketServerAction(new PacketServerAction.PlayerAction[] { new PacketServerAction.PlayerAction(player, PacketServerAction.Action.KICK, reson) });
		return writePacket(action);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> sendServerMessage(String target, String channel, DataBuffer buffer) {
		PacketServerMessage packet = new PacketServerMessage(channel, target, buffer);
		return writePacket(packet);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> sendServerMessage(ClientType target, String channel, DataBuffer buffer) {
		PacketServerMessage packet = new PacketServerMessage(channel, target, buffer);
		return writePacket(packet);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> sendPacket(ClientType type, Packet packet) {
		PacketForward p = new PacketForward(type, packet);
		return writePacket(p);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> sendPacket(String target, Packet packet) {
		PacketForward p = new PacketForward(target, packet);
		return writePacket(p);
	}

	public void updateServerStats() {
		handle.updateServerStats();
	}

	public Client getHandle() {
		return handle;
	}

	protected void changeUUID(LoadedPlayer player, UUID oldUUID, UUID newUUID) {
		uuidPlayers.remove(oldUUID);
		uuidPlayers.put(newUUID, player);
	}

	public ProgressFuture<PacketOutLobbyServer> getLobbies(GameRequest... games) {
		PacketInLobbyServerRequest q = new PacketInLobbyServerRequest(games);
		handle.writePacket(q);
		return new LobbyServerResponseFuture(handle, q);
	}

	public ProgressFuture<PacketOutTopTen> getTopTen(GameType game, StatsKey condition) {
		PacketInTopTenRequest r = new PacketInTopTenRequest(game, condition);
		handle.writePacket(r);
		return new TopTenResponseFuture(handle, r);
	}

	public ProgressFuture<Skin> getSkin(String player) {
		UUID requestUUID = UUID.randomUUID();
		PacketSkinRequest r = new PacketSkinRequest(requestUUID,new PacketSkinRequest.SkinRequest[]{new PacketSkinRequest.SkinRequest(Type.NAME, player, null)});
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[], Skin>(new SkinResponseFuture(handle, r, requestUUID)) {
			@Override
			public Skin transform(SkinResponse[] obj) {
				if(obj.length > 0)
					if(obj[0] == null)
						return new SteveSkin();
					else
						return obj[0].getSkin();
				return new SteveSkin();
			}
		};
	}

	public ProgressFuture<Skin[]> getSkin(String... players) {
		UUID requestUUID = UUID.randomUUID();
		PacketSkinRequest.SkinRequest[] requests = new PacketSkinRequest.SkinRequest[players.length];
		for(int i = 0;i<requests.length;i++){
			requests[i] = new PacketSkinRequest.SkinRequest(Type.NAME, players[i], null);
		}
		PacketSkinRequest r = new PacketSkinRequest(requestUUID,requests);
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[], Skin[]>(new SkinResponseFuture(handle, r, requestUUID)) {
			@Override
			public Skin[] transform(SkinResponse[] obj) {
				Skin[] out = new Skin[obj.length];
				for(int i = 0;i<obj.length;i++)
					out[i] = obj[i].getSkin();
				return out;
			}
		};
	}
	
	public ProgressFuture<Skin[]> getSkin(UUID... players) {
		UUID requestUUID = UUID.randomUUID();
		PacketSkinRequest.SkinRequest[] requests = new PacketSkinRequest.SkinRequest[players.length];
		for(int i = 0;i<requests.length;i++){
			requests[i] = new PacketSkinRequest.SkinRequest(Type.UUID, null, players[i]);
		}
		PacketSkinRequest r = new PacketSkinRequest(requestUUID,requests);
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[], Skin[]>(new SkinResponseFuture(handle, r, requestUUID)) {
			@Override
			public Skin[] transform(SkinResponse[] obj) {
				Skin[] out = new Skin[obj.length];
				for(int i = 0;i<obj.length;i++)
					out[i] = obj[i].getSkin();
				return out;
			}
		};
	}
	
	public ProgressFuture<Skin> getSkin(UUID player) {
		UUID requestUUID = UUID.randomUUID();
		PacketSkinRequest r = new PacketSkinRequest(requestUUID, new PacketSkinRequest.SkinRequest[]{new PacketSkinRequest.SkinRequest(Type.UUID, null, player)});
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[], Skin>(new SkinResponseFuture(handle, r, requestUUID)) {
			@Override
			public Skin transform(SkinResponse[] obj) {
				if(obj.length > 0)
					if(obj[0] == null)
						return new SteveSkin();
					else
						return obj[0].getSkin();
				return new SteveSkin();
			}
		};
	}
	
	/**
	 * 
	 * @param type == null than no update found
	 * @return
	 */
	public ProgressFuture<String> requestLanguageUpdate(LanguageType type,double curruntVersion){
		PacketLanguageRequest r = new PacketLanguageRequest(type, curruntVersion);
		handle.writePacket(r);
		return new LanguageUpdateFuture<>(handle, r);
	}
}

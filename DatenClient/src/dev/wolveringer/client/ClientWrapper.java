package dev.wolveringer.client;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import dev.wolveringer.arrays.CachedArrayList;
import dev.wolveringer.booster.BoosterType;
import dev.wolveringer.booster.NetworkBooster;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.ClientType;
import dev.wolveringer.client.futures.BoosterResposeFuture;
import dev.wolveringer.client.futures.FutureResponseTransformer;
import dev.wolveringer.client.futures.LanguageUpdateFuture;
import dev.wolveringer.client.futures.LobbyServerResponseFuture;
import dev.wolveringer.client.futures.PlayerIdResponseFuture;
import dev.wolveringer.client.futures.ReportResponseFuture;
import dev.wolveringer.client.futures.ServerStatusResponseFuture;
import dev.wolveringer.client.futures.SkinResponseFuture;
import dev.wolveringer.client.futures.TopTenResponseFuture;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import dev.wolveringer.dataserver.player.LanguageType;
import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketBoosterStatusRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketChatMessage;
import dev.wolveringer.dataserver.protocoll.packets.PacketChatMessage.TargetType;
import dev.wolveringer.dataserver.protocoll.packets.PacketForward;
import dev.wolveringer.dataserver.protocoll.packets.PacketInLobbyServerRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInLobbyServerRequest.GameRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInServerStatusRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketInTopTenRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketLanguageRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutLobbyServer;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus.Action;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutTopTen;
import dev.wolveringer.dataserver.protocoll.packets.PacketPlayerIdRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketReportEdit;
import dev.wolveringer.dataserver.protocoll.packets.PacketReportEdit.EditKey;
import dev.wolveringer.dataserver.protocoll.packets.PacketReportRequest;
import dev.wolveringer.skin.Skin;
import dev.wolveringer.skin.SteveSkin;
import dev.wolveringer.translation.TranslationManager;
import lombok.Getter;
import dev.wolveringer.dataserver.protocoll.packets.PacketServerAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketServerMessage;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinData.SkinResponse;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinRequest;
import dev.wolveringer.dataserver.protocoll.packets.PacketSkinRequest.Type;
import dev.wolveringer.report.ReportEntity;

public class ClientWrapper {
	protected Client handle;
	private static CachedArrayList<LoadedPlayer> players = new CachedArrayList<LoadedPlayer>(20, TimeUnit.MINUTES);

	public static void unloadAllPlayers() {
		players.clear();
	}

	@Getter
	private TranslationManager translationManager;

	public ClientWrapper(Client handle) {
		this.handle = handle;
		this.translationManager = new TranslationManager(this);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> writePacket(Packet packet) {
		return handle.writePacket(packet);
	}

	@Deprecated
	public LoadedPlayer getPlayer(String name) {
		for (LoadedPlayer player : new ArrayList<>(players))
			if (player != null)
				if (player.getName() != null)
					if (player.getName().equalsIgnoreCase(name)) {
						players.resetTime(player);
						return player;
					}
		LoadedPlayer player = new LoadedPlayer(this, name);
		players.add(player);
		return player;
	}

	@Deprecated
	public LoadedPlayer getPlayer(UUID uuid) {
		for (LoadedPlayer player : new ArrayList<>(players))
			if (player != null)
				if (player.getUUID() != null)
					if (player.getUUID().equals(uuid)) {
						players.resetTime(player);
						return player;
					}
		LoadedPlayer player = new LoadedPlayer(this, uuid);
		players.add(player);
		return player;
	}

	public LoadedPlayer getPlayer(int id) {
		for (LoadedPlayer player : new ArrayList<>(players))
			if (player != null)
				if (player.getPlayerId() == id) {
					players.resetTime(player);
					return player;
				}
		LoadedPlayer player = new LoadedPlayer(this, id);
		players.add(player);
		return player;
	}

	public LoadedPlayer getPlayerAndLoad(String name) {
		LoadedPlayer player = getPlayer(name);
		if (!player.isLoaded()) {
			player.load();
		}
		return player;
	}

	public LoadedPlayer getPlayerAndLoad(UUID uuid) {
		LoadedPlayer player = getPlayer(uuid);
		if (!player.isLoaded()) {
			player.load();
		}
		return player;
	}

	public LoadedPlayer getPlayerAndLoad(int id) {
		LoadedPlayer player = getPlayer(id);
		if (!player.isLoaded()) {
			player.load();
		}
		return player;
	}

	public void clearCacheForPlayer(LoadedPlayer player) {
		if (player == null)
			return;
		for (LoadedPlayer s : new ArrayList<>(players))
			if (player != null)
				if (s.equals(player))
					players.remove(s);
	}

	public ArrayList<LoadedPlayer> getPlayers() {
		return players;
	}

	public ServerStatusResponseFuture getServerStatus(dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus.Action action, String server) {
		return getServerStatus(action, server, false);
	}

	public ServerStatusResponseFuture getServerStatus(dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus.Action action, String server, boolean player) {
		PacketInServerStatusRequest p;
		if (action == Action.GAMETYPE)
			throw new RuntimeException("GAMETYPE isnt an spectial server");
		writePacket(p = new PacketInServerStatusRequest(action, server, player, null));
		return new ServerStatusResponseFuture(handle, p);
	}

	public ProgressFuture<int[]> getPlayerIds(String... player) {
		PacketPlayerIdRequest request = new PacketPlayerIdRequest(player);
		writePacket(request);
		return new PlayerIdResponseFuture(handle, request);
	}

	public ProgressFuture<int[]> getPlayerIds(UUID... player) {
		PacketPlayerIdRequest request = new PacketPlayerIdRequest(player);
		writePacket(request);
		return new PlayerIdResponseFuture(handle, request);
	}

	public ServerStatusResponseFuture getGameTypeServerStatus(GameType[] types, boolean player) {
		PacketInServerStatusRequest p;
		writePacket(p = new PacketInServerStatusRequest(dev.wolveringer.dataserver.protocoll.packets.PacketOutServerStatus.Action.GAMETYPE, null, player, types));
		return new ServerStatusResponseFuture(handle, p);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> sendMessage(int playerId, String message) {
		PacketChatMessage p = new PacketChatMessage(message, new PacketChatMessage.Target[] { new PacketChatMessage.Target(TargetType.PLAYER, null, playerId + "") });
		return writePacket(p);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> brotcastMessage(String permission, String message) {
		PacketChatMessage p = new PacketChatMessage(message, new PacketChatMessage.Target[] { new PacketChatMessage.Target(TargetType.BROTCAST, permission, message) });
		return writePacket(p);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> kickPlayer(int player, String reson) {
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
		PacketSkinRequest r = new PacketSkinRequest(requestUUID, new PacketSkinRequest.SkinRequest[] { new PacketSkinRequest.SkinRequest(Type.NAME, player, null, -1) });
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[], Skin>(new SkinResponseFuture(handle, r, requestUUID)) {
			@Override
			public Skin transform(SkinResponse[] obj) {
				if (obj.length > 0)
					if (obj[0] == null)
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
		for (int i = 0; i < requests.length; i++) {
			requests[i] = new PacketSkinRequest.SkinRequest(Type.NAME, players[i], null, -1);
		}
		PacketSkinRequest r = new PacketSkinRequest(requestUUID, requests);
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[], Skin[]>(new SkinResponseFuture(handle, r, requestUUID)) {
			@Override
			public Skin[] transform(SkinResponse[] obj) {
				Skin[] out = new Skin[obj.length];
				for (int i = 0; i < obj.length; i++)
					out[i] = obj[i].getSkin();
				return out;
			}
		};
	}

	public ProgressFuture<Skin[]> getSkin(UUID... players) {
		UUID requestUUID = UUID.randomUUID();
		PacketSkinRequest.SkinRequest[] requests = new PacketSkinRequest.SkinRequest[players.length];
		for (int i = 0; i < requests.length; i++) {
			requests[i] = new PacketSkinRequest.SkinRequest(Type.UUID, null, players[i], -1);
		}
		PacketSkinRequest r = new PacketSkinRequest(requestUUID, requests);
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[], Skin[]>(new SkinResponseFuture(handle, r, requestUUID)) {
			@Override
			public Skin[] transform(SkinResponse[] obj) {
				Skin[] out = new Skin[obj.length];
				for (int i = 0; i < obj.length; i++)
					out[i] = obj[i].getSkin();
				return out;
			}
		};
	}

	public ProgressFuture<Skin> getSkin(UUID player) {
		UUID requestUUID = UUID.randomUUID();
		PacketSkinRequest r = new PacketSkinRequest(requestUUID, new PacketSkinRequest.SkinRequest[] { new PacketSkinRequest.SkinRequest(Type.UUID, null, player, -1) });
		handle.writePacket(r);
		return new FutureResponseTransformer<SkinResponse[], Skin>(new SkinResponseFuture(handle, r, requestUUID)) {
			@Override
			public Skin transform(SkinResponse[] obj) {
				if (obj.length > 0)
					if (obj[0] == null)
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
	public ProgressFuture<String> requestLanguageUpdate(LanguageType type, double curruntVersion) {
		PacketLanguageRequest r = new PacketLanguageRequest(type, curruntVersion);
		handle.writePacket(r);
		return new LanguageUpdateFuture<>(handle, r);
	}

	public ProgressFuture<ReportEntity[]> getReportEntity(PacketReportRequest.RequestType type, int value) {
		PacketReportRequest request = new PacketReportRequest(type, value);
		handle.writePacket(request);
		return new ReportResponseFuture(handle, request);
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> closeReport(ReportEntity e) {
		return writePacket(new PacketReportEdit(EditKey.CLOSE, e.getReportId(), -1, null, null));
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> addReportWorker(int reportId, int workerId) {
		return writePacket(new PacketReportEdit(EditKey.ADD_WORKER, reportId, workerId, null, null));
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> closeReportWorker(int reportId, int workerId) {
		return writePacket(new PacketReportEdit(EditKey.DONE_WORKER, reportId, workerId, null, null));
	}

	public ProgressFuture<PacketOutPacketStatus.Error[]> createReport(int playerId, int target, String reson, String info) {
		return writePacket(new PacketReportEdit(EditKey.CREATE, playerId, target, reson, info));
	}
	public ProgressFuture<NetworkBooster> getNetworkBooster(BoosterType type){
		Packet p;
		handle.writePacket(p = new PacketBoosterStatusRequest(type));
		return new BoosterResposeFuture(handle, p, -1,type);
	}
	public ProgressFuture<NetworkBooster> getNetworkBoosterInformation(BoosterType type,int playerId){
		Packet p;
		handle.writePacket(p = new PacketBoosterStatusRequest(playerId, type));
		return new BoosterResposeFuture(handle, p, -1,BoosterType.NONE);
	}
}

package dev.wolveringer.client;

import java.util.HashMap;
import java.util.UUID;

import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.ClientType;
import dev.wolveringer.client.futures.NameFutureResponseFuture;
import dev.wolveringer.client.futures.ServerStatusResponseFuture;
import dev.wolveringer.client.futures.StatusResponseFuture;
import dev.wolveringer.client.futures.UUIDFuture;
import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketChatMessage;
import dev.wolveringer.dataclient.protocoll.packets.PacketChatMessage.TargetType;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse.UUIDKey;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutNameRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerStatusRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutUUIDRequest;
import dev.wolveringer.dataclient.protocoll.packets.PacketServerAction;
import dev.wolveringer.dataclient.protocoll.packets.PacketServerMessage;
import dev.wolveringer.dataclient.protocoll.packets.PacketInServerStatus.Action;

public class ClientWrapper {
	protected Client handle;
	private HashMap<String, LoadedPlayer> players = new HashMap<>();
	private HashMap<UUID, LoadedPlayer> uuidPlayers = new HashMap<>();
	
	public ClientWrapper(Client handle) {
		this.handle = handle;
	}
	
	public UUIDFuture getUUID(String...players){
		Packet packet = new PacketOutUUIDRequest(players);
		UUIDFuture future = new UUIDFuture(handle, packet, players);
		handle.writePacket(packet);
		return future;
	}
	public StatusResponseFuture writePacket(Packet packet){
		StatusResponseFuture f = new StatusResponseFuture(handle, packet.getPacketUUID());
		handle.writePacket(packet);
		return f;
	}
	
	public LoadedPlayer getPlayer(String name){
		if(players.containsKey(name))
			return players.get(name);
		LoadedPlayer player = new LoadedPlayer(this,name);
		players.put(name, player);
		return player;
	}
	public LoadedPlayer getPlayer(UUID uuid){
		if(uuidPlayers.containsKey(uuid))
			return uuidPlayers.get(uuid);
		String name = getName(uuid);
		if(name == null)
			throw new NullPointerException("Player not found");
		return getPlayer(name);
	}
	
	public LoadedPlayer getPlayerAndLoad(String name){
		LoadedPlayer player = getPlayer(name);
		if(!player.isLoaded())
			player.load();
		return player;
	}
	public LoadedPlayer getPlayerAndLoad(UUID name){
		LoadedPlayer player = getPlayer(name);
		if(!player.isLoaded())
			player.load();
		return player;
	}
	
	private String getName(UUID uuid){
		PacketOutNameRequest r = new PacketOutNameRequest(new UUID[]{uuid});
		NameFutureResponseFuture f = new NameFutureResponseFuture(handle, r, new UUID[]{uuid});
		handle.writePacket(r);
		UUIDKey[] key = f.getSync();
		if(key.length == 1)
			return key[0].getName();
		return null;
	}
	public ServerStatusResponseFuture getServerStatus(Action action,String server){
		return getServerStatus(action, server, false);
	}
	public ServerStatusResponseFuture getServerStatus(Action action,String server,boolean player){
		PacketOutServerStatusRequest p;
		writePacket(p=new PacketOutServerStatusRequest(action, server, player));
		return new ServerStatusResponseFuture(handle, p);
	}
	public StatusResponseFuture sendMessage(UUID player,String message){
		PacketChatMessage p = new PacketChatMessage(message, new PacketChatMessage.Target[]{new PacketChatMessage.Target(TargetType.PLAYER, null, player.toString())});
		return writePacket(p);
	}
	public StatusResponseFuture brotcastMessage(String permission,String message){
		PacketChatMessage p = new PacketChatMessage(message, new PacketChatMessage.Target[]{new PacketChatMessage.Target(TargetType.BROTCAST, permission, message)});
		return writePacket(p);
	}
	public StatusResponseFuture kickPlayer(UUID player,String reson){
		PacketServerAction action = new PacketServerAction(new PacketServerAction.PlayerAction[]{new PacketServerAction.PlayerAction(player, dev.wolveringer.dataclient.protocoll.packets.PacketServerAction.Action.KICK, reson)});
		return writePacket(action);
	}
	public StatusResponseFuture sendServerMessage(String target,String channel,DataBuffer buffer){
		PacketServerMessage packet = new PacketServerMessage(channel, target, buffer);
		return writePacket(packet);
	}
	public StatusResponseFuture sendServerMessage(ClientType target,String channel,DataBuffer buffer){
		PacketServerMessage packet = new PacketServerMessage(channel, target, buffer);
		return writePacket(packet);
	}
}

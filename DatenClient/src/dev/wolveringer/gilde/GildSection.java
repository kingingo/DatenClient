package dev.wolveringer.gilde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildCostumDataAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMemeberAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildUpdateSectionStatus;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus.Error;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMemeberAction.Action;
import dev.wolveringer.gilde.GildeType;
import dev.wolveringer.nbt.NBTTagCompound;
import lombok.Getter;

public class GildSection {
	@Getter
	private Gilde handle;
	@Getter
	private GildeType type;
	@Getter
	protected boolean active;
	private GildSectionPermission permissions = new GildSectionPermission(this);
	private NBTTagCompound costumData;
	@Getter
	private GildSectionMoney money = new GildSectionMoney(this);
	
	protected ArrayList<Integer> players = new ArrayList<>();
	@Getter
	protected ArrayList<Integer> requestedPlayer = new ArrayList<>();
	
	public GildSection(Gilde handle, GildeType type,boolean active) {
		this.handle = handle;
		this.type = type;
		this.active = active;
		if(active)
			this.money.init();
	}

	public void reloadDataSync(){
		costumData = handle.getConnection().getGildeData(this).getSync();
	}
	
	public GildSectionPermission getPermission(){
		return permissions;
	}
	
	public List<Integer> getPlayers() {
		return Collections.unmodifiableList(players);
	}
	
	public void addRequest(LoadedPlayer player){
		if(!requestedPlayer.contains(new Integer(player.getPlayerId()))){
			requestedPlayer.add(player.getPlayerId());
			handle.getConnection().writePacket(new PacketGildMemeberAction(handle.getUuid(), type, player.getPlayerId(), Action.INVITE, null));
		}
	}
	
	public void removeRequest(LoadedPlayer player){
		if(requestedPlayer.contains(new Integer(player.getPlayerId()))){
			requestedPlayer.remove(new Integer(player.getPlayerId()));
			handle.getConnection().writePacket(new PacketGildMemeberAction(handle.getUuid(), type, player.getPlayerId(), Action.REMOVE_INVITE, null));
		}
	}
	
	public ProgressFuture<Error[]> acceptRequest(LoadedPlayer player){
		if(requestedPlayer.contains(new Integer(player.getPlayerId()))){
			requestedPlayer.remove(new Integer(player.getPlayerId()));
			return handle.getConnection().writePacket(new PacketGildMemeberAction(handle.getUuid(), type, player.getPlayerId(), Action.ACCEPT_REQUEST, null));
		}
		return null;
	}
	
	public void kickPlayer(LoadedPlayer player){
		if(players.contains(new Integer(player.getPlayerId()))){
			players.remove(new Integer(player.getPlayerId()));
			handle.getConnection().writePacket(new PacketGildMemeberAction(handle.getUuid(), type, player.getPlayerId(), Action.KICK, null));
		}
	}
	
	public void addMemeber(LoadedPlayer player){
		if(!players.contains(new Integer(player.getPlayerId()))){
			players.add(new Integer(player.getPlayerId()));
			handle.getConnection().writePacket(new PacketGildMemeberAction(handle.getUuid(), type, player.getPlayerId(), Action.CHANGE_GROUP, "default"));
		}
	}
	
	public NBTTagCompound getCostumData() {
		return costumData;
	}
	public void saveCostumData(){
		handle.getConnection().writePacket(new PacketGildCostumDataAction(handle.getUuid(), type, costumData));
	}
	
	public ProgressFuture<Error[]> setActive(boolean active) {
		if(this.active == active)
			return null;
		this.active = active;
		return handle.getConnection().writePacket(new PacketGildUpdateSectionStatus(handle.getUuid(), type, active));
	}
	
	public LoadedPlayer getStatsPlayer(){
		return handle.getConnection().getPlayerAndLoad(handle.getUuid().toString().toLowerCase()+"_stats");
	}
}

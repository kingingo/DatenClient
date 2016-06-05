package dev.wolveringer.gilde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildCostumDataAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMemeberAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildMemeberAction.Action;
import dev.wolveringer.nbt.NBTTagCompound;
import lombok.Getter;

public class GildSection {
	@Getter
	private Gilde handle;
	@Getter
	private GildeType type;
	@Getter
	private boolean active;
	private GildSectionPermission permissions = new GildSectionPermission(this);
	private NBTTagCompound costumData;
	
	protected ArrayList<Integer> players = new ArrayList<>();
	
	public GildSection(Gilde handle, GildeType type,boolean active) {
		this.handle = handle;
		this.type = type;
		this.active = active;
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
	
	public void kickPlayer(LoadedPlayer player){
		if(players.contains(new Integer(player.getPlayerId()))){
			players.remove(new Integer(player.getPlayerId()));
			handle.getConnection().writePacket(new PacketGildMemeberAction(handle.getUuid(), type, player.getPlayerId(), Action.KICK, null));
		}
	}
	
	public NBTTagCompound getCostumData() {
		return costumData;
	}
	public void saveCostumData(){
		handle.getConnection().writePacket(new PacketGildCostumDataAction(handle.getUuid(), type, costumData));
	}
}

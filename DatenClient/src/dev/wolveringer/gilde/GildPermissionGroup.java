package dev.wolveringer.gilde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildPermissionEdit;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildPermissionEdit.Action;
import lombok.Getter;

public class GildPermissionGroup {
	@Getter
	private GildSectionPermission handle;
	
	@Getter
	private String name;
	private ArrayList<String> permissions;
	
	public GildPermissionGroup(GildSectionPermission handle,String name) {
		this.name = name;
		this.handle = handle;
	}
	
	private synchronized void init(){
		if(permissions == null){
			ClientWrapper connection = handle.getHandle().getHandle().getConnection();
			permissions = connection.getPermissions(this).getSync();
		}
	}
	
	public void reload(){
		permissions = null;
		init();
	}
	
	public List<String> getPermissions() {
		return Collections.unmodifiableList(permissions);
	}
	
	public void removePermission(String permission){
		init();
		if(permissions.remove(permission)){
			handle.getHandle().getHandle().getConnection().writePacket(new PacketGildPermissionEdit(handle.getHandle().getHandle().getUuid(), handle.getHandle().getType(), Action.REMOVE_PERMISSION, name, permission));
		}
	}
	public void addPermission(String permission){
		init();
		if(!permissions.contains(permission)){
			handle.getHandle().getHandle().getConnection().writePacket(new PacketGildPermissionEdit(handle.getHandle().getHandle().getUuid(), handle.getHandle().getType(), Action.ADD_PERMISSION, name, permission));
			permissions.add(permission);
		}
	}
	public boolean hasPermission(String permission){
		init();
		return permissions.contains(permission);
	}
	public ArrayList<LoadedPlayer> getPlayers(){
		ArrayList<LoadedPlayer> players = new ArrayList<>();
		for(Entry<Integer, String> player : handle.players.entrySet())
			if(player.getValue().equalsIgnoreCase(name))
				players.add(handle.getHandle().getHandle().getConnection().getPlayer(player.getKey()));
		return players;
	}
}

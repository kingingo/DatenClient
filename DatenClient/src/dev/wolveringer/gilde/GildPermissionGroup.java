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
	private static final String ITEM_ID_PREFIX = "permission.itemid.";
	@Getter
	private GildSectionPermission handle;
	
	@Getter
	private String name;
	@Getter
	private Integer itemId = 1;
	protected ArrayList<String> permissions;
	@Getter
	private boolean defaultGroup;
	public GildPermissionGroup(GildSectionPermission handle,String name) {
		this.name = name;
		this.handle = handle;
		init();	
	}
	
	private synchronized void init(){
		if(permissions == null){
			ClientWrapper connection = handle.getHandle().getHandle().getConnection();
			permissions = new ArrayList<>(connection.getPermissions(this).getSync());
		}
		for(String s : new ArrayList<>(getPermissions())){
			if(s.startsWith("permission.itemid.")){
				permissions.remove(s);
				itemId = Integer.parseInt(s.replaceAll(ITEM_ID_PREFIX, ""));
			}
			if(s.equals("group.default")){
				defaultGroup = true;
				permissions.remove(s);
			}
		}
	}
	
	public void setItemId(int id){
		removePermission(ITEM_ID_PREFIX+itemId);
		this.itemId = id;
		addPermission(ITEM_ID_PREFIX+itemId);
	}
	
	public void reload(){
		permissions = null;
	}
	
	public List<String> getPermissions() {
		return Collections.unmodifiableList(permissions);
	}
	
	public List<GildePermissions> getEnumPermissions() {
		ArrayList<GildePermissions> out = new ArrayList<>();
		for(String perm : getPermissions()){
			out.add(GildePermissions.getPermission(perm));
		}
		return out;
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
		for(Entry<Integer, String> player : handle.players.entrySet()){
			if(player.getValue().equalsIgnoreCase(name))
				players.add(handle.getHandle().getHandle().getConnection().getPlayerAndLoad(player.getKey()));
		}
		return players;
	}
}

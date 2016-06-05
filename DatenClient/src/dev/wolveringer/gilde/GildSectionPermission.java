package dev.wolveringer.gilde;

import java.util.ArrayList;
import java.util.HashMap;

import dev.wolveringer.client.LoadedPlayer;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildPermissionEdit;
import dev.wolveringer.dataserver.protocoll.packets.PacketGildPermissionEdit.Action;
import lombok.Getter;

public class GildSectionPermission {
	@Getter
	private GildSection handle;

	private ArrayList<GildPermissionGroup> groups;
	protected HashMap<Integer, String> players = new HashMap<>();

	public GildSectionPermission(GildSection handle) {
		this.handle = handle;
	}

	private synchronized void init() {
		if (groups == null) {
			ArrayList<String> names = handle.getHandle().getConnection().getGildGroups(this).getSync();
			groups = new ArrayList<>();
			for (String s : names)
				groups.add(new GildPermissionGroup(this, s));
		}
	}

	public synchronized void reload(){
		if(groups == null)
			throw new RuntimeException("Cant reload before loading");
		ArrayList<String> names = handle.getHandle().getConnection().getGildGroups(this).getSync();
		for (String s : names)
			if(getGroup(s) == null)
				groups.add(new GildPermissionGroup(this, s));
		for(GildPermissionGroup g : new ArrayList<>(groups))
			if(!names.contains(g.getName()))
				groups.remove(g);
	}
	
	public ArrayList<String> getGroups() {
		init();
		ArrayList<String> out = new ArrayList<>();
		for (GildPermissionGroup g : groups)
			out.add(g.getName());
		return out;
	}

	public GildPermissionGroup getGroup(String name) {
		init();
		for (GildPermissionGroup g : groups)
			if (g.getName().equalsIgnoreCase(name))
				return g;
		return null;
	}

	public GildPermissionGroup getGroup(LoadedPlayer player) {
		init();
		return getGroup(players.get(new Integer(player.getPlayerId())));
	}
	
	public void setGroup(LoadedPlayer player,GildPermissionGroup group){
		GildPermissionGroup old = getGroup(player);
		if(old != null && old.equals(group))
			return;
		if(old != null){
			handle.getHandle().getConnection().writePacket(new PacketGildPermissionEdit(handle.getHandle().getUuid(), handle.getType(), Action.REMOVE_GROUP, String.valueOf(player.getPlayerId()), old.getName()));
			players.remove(new Integer(player.getPlayerId()));
		}else
		{
			if(!handle.players.contains(new Integer(player.getPlayerId())))
				throw new IllegalArgumentException("Cant set permission of a player whitch isnt in gild.");
		}
		if(group != null){
			handle.getHandle().getConnection().writePacket(new PacketGildPermissionEdit(handle.getHandle().getUuid(), handle.getType(), Action.ADD_GROUP, String.valueOf(player.getPlayerId()), group.getName()));
			players.put(new Integer(player.getPlayerId()), group.getName());
		}
	}
}

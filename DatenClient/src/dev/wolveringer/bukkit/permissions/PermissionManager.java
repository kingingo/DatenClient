package dev.wolveringer.bukkit.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionManager{
	private static PermissionManager manager;

	public static PermissionManager getManager() {
		return manager;
	}

	public static void setManager(PermissionManager manager) {
		PermissionManager.manager = manager;
	}

	private ArrayList<Group> groups = new ArrayList<>();
	private HashMap<UUID, PermissionPlayer> user = new HashMap<>();
	protected JavaPlugin plugin;
	protected PermissionChannelHandler handler;
	
	public PermissionManager(JavaPlugin plugin) {
		this.plugin = plugin;
		this.handler = new PermissionChannelHandler(this);
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "permission", handler);
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "permission");
	}

	public void loadPlayer(Player p,UUID player) {
		if (!user.containsKey(player))
			user.put(player, new PermissionPlayer(p,this, player));
	}

	public PermissionPlayer getPlayer(UUID player) {
		return user.get(player);
	}

	public boolean hasPermission(Player player, String permission) {
		return hasPermission(player.getUniqueId(), permission);
	}
	public boolean hasPermission(Player player, PermissionType teamMessage) {
		return hasPermission(player.getUniqueId(), teamMessage.getPermissionToString());
	}
	public boolean hasPermission(Player player, PermissionType teamMessage,boolean message) {
		return hasPermission(player, teamMessage.getPermissionToString(), message);
	}
	
	public boolean hasPermission(Player player, String permission, boolean message) {
		boolean perm = hasPermission(player.getUniqueId(), permission);
		if (message && !perm) ;
			//player.sendMessage(Language.getText(player, "PREFIX") + "§cYou don't have permission to do that."); //TODO fix error
		return perm;
	}

	public boolean hasPermission(UUID uuid, PermissionType permission) {
		return hasPermission(uuid, permission.getPermissionToString());
	}

	public boolean hasPermission(UUID uuid, String permission) {
		if(!user.containsKey(uuid))
			return false;
		return user.get(uuid).hasPermission(permission);
	}

	public Group getGroup(String name) {
		for(Group g : groups)
			if(g.getName().equalsIgnoreCase(name))
				return g;
		return null;
	}
	
	public Group loadGroup(String name){
		if(getGroup(name) == null){
			groups.add(new Group(this, name));
		}
		return getGroup(name);
	}
}

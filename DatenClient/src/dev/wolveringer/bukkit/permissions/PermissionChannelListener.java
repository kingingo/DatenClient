package dev.wolveringer.bukkit.permissions;

import java.util.UUID;

import dev.wolveringer.dataserver.protocoll.DataBuffer;

public interface PermissionChannelListener {
	public void handle(UUID fromPacket,DataBuffer buffer);
}

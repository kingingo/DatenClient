package dev.wolveringer.bukkit.permissions;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;

public interface PermissionChannelListener {
	public void handle(UUID fromPacket,DataBuffer buffer);
}

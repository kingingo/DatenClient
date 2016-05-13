package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.PacketHandleErrorException;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.PacketListener;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;

public abstract class PacketResponseFuture<T> extends BaseProgressFuture<T> implements PacketListener{
	private static final long defaultTimeout = 10*1000;
	private Client client;
	private UUID handle;
	private long timeout = defaultTimeout;
	private long start = System.currentTimeMillis();
	
	public PacketResponseFuture(Client client,Packet handeling) {
		if(handeling != null)
			this.handle = handeling.getPacketUUID();
		this.client = client;
		client.getHandlerBoss().addListener(this);
	}
	
	@Override
	protected void done(T response) {
		client.getHandlerBoss().removeListener(this);
		super.done(response);
	}
	@Override
	protected void done(PacketHandleErrorException e) {
		client.getHandlerBoss().removeListener(this);
		super.done(e);
	}
	
	@Override
	public void handle(Packet packet) {
		if(start+timeout<System.currentTimeMillis()){
			done(new PacketHandleErrorException(new PacketOutPacketStatus(handle, new PacketOutPacketStatus.Error[]{new PacketOutPacketStatus.Error(1, "PacketResponseFuture -> timeout")})));
			return;
		}
		if(packet instanceof PacketOutPacketStatus && ((PacketOutPacketStatus) packet).getPacketId().equals(this.handle)){
			done(new PacketHandleErrorException((PacketOutPacketStatus) packet));
		}
		else
			handlePacket(packet);
	}
	
	public abstract void handlePacket(Packet packet);
}

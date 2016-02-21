package dev.wolveringer.client.futures;

import java.util.UUID;

import dev.wolveringer.client.PacketHandleErrorException;
import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.PacketListener;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPacketStatus;

public abstract class PacketResponseFuture<T> extends ProgressFuture<T> implements PacketListener{
	private Client client;
	private UUID handle;
	
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
	public void handle(Packet packet) {
		if(packet instanceof PacketInPacketStatus && ((PacketInPacketStatus) packet).getPacketId().equals(this.handle)){
			done(new PacketHandleErrorException((PacketInPacketStatus) packet));
		}
		else
			handlePacket(packet);
	}
	
	public abstract void handlePacket(Packet packet);
}

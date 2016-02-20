package dev.wolveringer.client.futures;

import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.client.connection.PacketListener;

public abstract class PacketResponseFuture<T> extends ProgressFuture<T> implements PacketListener{
	private Client client;
	
	public PacketResponseFuture(Client client) {
		this.client = client;
		client.getHandlerBoss().addListener(this);
	}
	
	@Override
	protected void done(T response) {
		client.getHandlerBoss().removeListener(this);
		super.done(response);
	}
}

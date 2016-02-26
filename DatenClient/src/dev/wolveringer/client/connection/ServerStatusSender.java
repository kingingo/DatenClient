package dev.wolveringer.client.connection;

import dev.wolveringer.client.threadfactory.ThreadFactory;
import dev.wolveringer.client.threadfactory.ThreadRunner;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutServerStatus;

public class ServerStatusSender {
	private ThreadRunner thread;
	private ServerInformations i;
	private Client owner;
	private boolean active;
	
	public ServerStatusSender(Client owner,ServerInformations infos) {
		this.i = infos;
		this.owner = owner;
		this.thread = ThreadFactory.getFactory().createThread(new Runnable() {
			@Override
			public void run() {
				while (active && owner.socket.isConnected() && owner.isConnected()) {
					owner.writePacket(i.getStatus());
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
					}
				}
			}
		});
	}
	public void start(){
		active = true;
		thread.start();
	}
}

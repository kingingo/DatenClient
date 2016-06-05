package dev.wolveringer.client.connection;

import dev.wolveringer.client.threadfactory.ThreadFactory;
import dev.wolveringer.client.threadfactory.ThreadRunner;
import dev.wolveringer.dataserver.protocoll.packets.PacketPing;
import dev.wolveringer.dataserver.protocoll.packets.PacketPong;

public class TimeOutThread {
	private ThreadRunner runner;
	private Client owner;
	private long maxTime = 8000;
	private boolean active = false;

	public TimeOutThread(Client cleint) {
		this.owner = cleint;
		init();
	}

	private void init() {
		runner = ThreadFactory.getFactory().createThread(new Runnable() {
			@Override
			public void run() {
				while (owner.isConnected() && active) {
					owner.getPingManager().ping();
					int ping = owner.getPingManager().getCurrentPing();
					if (ping > maxTime && ping != -1) {
						owner.disconnect("Client ->  Server -> Timeout! (Clientbased!)");
						System.out.println("Client timed out (" + (ping) + ")");
						return;
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
				}
			}
		});
	}

	public void start() {
		active = true;
		runner.start();
	}

	public void stop() {
		active = false;
		runner.stop();
	}
}

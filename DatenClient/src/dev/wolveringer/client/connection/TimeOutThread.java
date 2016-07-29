package dev.wolveringer.client.connection;

import dev.wolveringer.thread.ThreadFactory;
import dev.wolveringer.thread.ThreadRunner;

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
						ThreadFactory.getFactory().createThread(()->{ //own thread will be killed forcely
							int trys = 0;
							while (true) {
								try{
									System.out.println("Client timed out (" + (ping) + "). Disconnecting..");
									owner.disconnect("Client ->  Server -> Timeout! (Clientbased!)");
									System.out.println("Client timed out (" + (ping) + "). Disceonnected");
									return;
								}catch(Exception e){
									e.printStackTrace();
									System.err.println("Cant disconnect!");
								}
								if(trys > 10){
									System.out.println("Force System to exit! Cant disconnect");
									System.exit(-2);
								}
								trys++;
							}
						}).start();
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

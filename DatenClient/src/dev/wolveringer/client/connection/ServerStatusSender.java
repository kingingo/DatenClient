package dev.wolveringer.client.connection;

import dev.wolveringer.client.threadfactory.ThreadFactory;
import dev.wolveringer.client.threadfactory.ThreadRunner;

public class ServerStatusSender {
	private ThreadRunner thread;
	private ServerInformations i;
	private Client owner;
	private boolean active;
	
	public ServerStatusSender(Client owner,ServerInformations infos) {
		this.i = infos;
		this.owner = owner;
	}
	public void start(){
		if(thread != null){
			try{
				thread.stop();
			}catch(Exception e){}
		}
		active = true;
		this.thread = ThreadFactory.getFactory().createThread(new Runnable() {
			@Override
			public void run() {
				while (active && owner.isConnected()) {
					updateServerStats();
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
					}
				}
				thread = null;
			}
		});
		thread.start();
	}
	public void updateServerStats() {
		owner.writePacket(i.getStatus());
	}
	public void stop() {
		active = false;
	}
}

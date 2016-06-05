package dev.wolveringer.client.connection;

import dev.wolveringer.client.threadfactory.ThreadFactory;
import dev.wolveringer.client.threadfactory.ThreadRunner;

public class ServerStatusSender {
	private ThreadRunner thread;
	private ServerInformations i;
	private Client owner;
	private boolean active;
	private int sleepTime = 1000;
	
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
				System.out.println("Start to sending updateinformations from: "+i.getClass().getName());
				while (active && owner.isConnected()) {
					updateServerStats();
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Stopping sending updates States: "+active+":"+owner.isConnected());
				thread = null;
			}
		});
		thread.start();
	}
	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	public int getSleepTime() {
		return sleepTime;
	}
	public void updateServerStats() {
		owner.writePacket(i.getStatus());
	}
	public void stop() {
		active = false;
	}
}

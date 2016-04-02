package dev.wolveringer.client.connection;

import dev.wolveringer.client.threadfactory.ThreadFactory;
import dev.wolveringer.client.threadfactory.ThreadRunner;
import dev.wolveringer.dataserver.protocoll.packets.PacketPingPong;

public class TimeOutThread {
	ThreadRunner runner;
	Client owner;
	long maxTime = 8000;
	boolean active = false;
	public TimeOutThread(Client cleint) {
		this.owner = cleint;
		init();
	}
	
	private void init(){
		runner = ThreadFactory.getFactory().createThread(new Runnable() {
			@Override
			public void run() {
				while (owner.socket.isConnected() && active) {
					try{
						owner.writePacket(new PacketPingPong(System.currentTimeMillis()));
					}catch(Exception e){
					
					}
					if(System.currentTimeMillis()-owner.lastPingTime>maxTime&& owner.lastPingTime != -1){
						owner.disconnect("Server -> Client -> Timeout!");
						System.out.println("Client timed out ("+(System.currentTimeMillis()-owner.lastPingTime)+")");
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
	public void start(){
		active = true;
		runner.start();
	}

	public void stop() {
		active = false;
		runner.stop();
	}
}

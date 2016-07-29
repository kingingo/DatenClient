package dev.wolveringer.client.connection;

import java.io.IOException;
import java.io.InputStream;

import dev.wolveringer.client.debug.Debugger;
import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.Packet.PacketDirection;
import dev.wolveringer.thread.ThreadFactory;
import dev.wolveringer.thread.ThreadRunner;

public class ReaderThread {
	public static interface Unsave {
		public InputStream getInputsteam();
		public ThreadRunner getThread();
	}
	
	private Client client;
	private InputStream in;
	private ThreadRunner reader;
	private boolean active;
	private Unsave unsave = new Unsave() {
		@Override
		public ThreadRunner getThread() {
			return reader;
		}
		
		@Override
		public InputStream getInputsteam() {
			return in;
		}
	};
	
	public Unsave unsave() {
		return unsave;
	}
	
	public ReaderThread(Client client, InputStream in) {
		this.client = client;
		this.in = in;
		init();
	}

	private void init() {
		reader = ThreadFactory.getFactory().createThread(new Runnable() {
			@Override
			public void run() {
				try {
					while (active) {
						if(!client.socket.isConnected()){
							client.closePipeline(false);
							client.getExternalHandler().disconnected();
							return;
						}
						if(in.available() > 0)
							readPacket();
						else
							Thread.sleep(10);
					}
				} catch (Exception e) {
					if("sleep interrupted".equalsIgnoreCase(e.getMessage()))
						return;
					if (!active)
						return;
					Debugger.debug("Reader Broken");
					e.printStackTrace();
					close0();
					client.getExternalHandler().disconnected();
				}
			}
		});
	}

	long lastReset = System.currentTimeMillis();
	int handeled = 0;
	
	private synchronized void readPacket() throws IOException {
		long start = System.currentTimeMillis();
		int length = (in.read() << 24) & 0xff000000 | (in.read() << 16) & 0x00ff0000 | (in.read() << 8) & 0x0000ff00 | (in.read()) & 0x000000ff;
		if (length <= 0 || length > 650000) {
			System.out.println("Reader index wrong (Wrong length ("+length+"))");
			client.closePipeline(true);
			return;
		}
		byte[] bbuffer = new byte[length];
		for(int i = 0;i<bbuffer.length;i++){
			bbuffer[i] = (byte) in.read();
		}
		
		DataBuffer buffer = new DataBuffer(bbuffer);
		ThreadFactory.getFactory().createThread(new Runnable() {
			public void run() {
				int packetId = buffer.readInt();
				Packet packet = Packet.createPacket(packetId, buffer,PacketDirection.TO_CLIENT);
				try{
					client.getHandlerBoss().handle(packet);	
				}catch(Exception e){
					System.out.println("Error while handeling packet "+packet);
					e.printStackTrace();
				}
				handeled++;
			}
		}).start();
		if(lastReset+10*1000 < System.currentTimeMillis()){
			Debugger.debug("Handeling "+(handeled/10)+" Packet per second!");
			handeled = 0;
			lastReset = System.currentTimeMillis();
		}
		long end = System.currentTimeMillis();
		Debugger.debug("Readed packet in "+(end-start)+"ms");
	}

	public void start() {
		if (!active) {
			active = true;
			reader.start();
		}
	}

	public void close() {
		if (active)
			close0();
	}

	private void close0() {
		active = false;
		if (in != null)
			try {
				in.close();
			} catch (Exception e) {
			}
		if (reader != null) {
			reader.stop();
		}
		client.closePipeline(false);
	}
}

package dev.wolveringer.client.connection;

import java.io.IOException;
import java.io.InputStream;

import dev.wolveringer.client.threadfactory.ThreadFactory;
import dev.wolveringer.client.threadfactory.ThreadRunner;
import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.Packet.PacketDirection;

public class ReaderThread {
	private Client client;
	private InputStream in;
	private ThreadRunner reader;
	private boolean active;

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
							client.closePipeline();
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
					e.printStackTrace();
					System.err.println("Reader Broken");
					e.printStackTrace();
					close0();
				}
			}
		});
	}

	private void readPacket() throws IOException {
		int length = (in.read() << 24) & 0xff000000 | (in.read() << 16) & 0x00ff0000 | (in.read() << 8) & 0x0000ff00 | (in.read() << 0) & 0x000000ff;
		if (length <= 0) {
			System.out.println("Reader index wrong (Wrong length)");
			return;
		}
		byte[] bbuffer = new byte[length];
		in.read(bbuffer);
		DataBuffer buffer = new DataBuffer(bbuffer);
		Packet packet = Packet.createPacket(buffer.readInt(), buffer,PacketDirection.TO_CLIENT);
		client.getHandlerBoss().handle(packet);
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
		client.closePipeline();
	}

}

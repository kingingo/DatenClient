package dev.wolveringer.client.connection;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import dev.wolveringer.client.debug.Debugger;
import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.Packet.PacketDirection;
import dev.wolveringer.thread.ThreadFactory;
import dev.wolveringer.thread.ThreadRunner;

public class SocketWriter {
	public static interface Unsave {
		public OutputStream getOutputStream();
		public ThreadRunner getThread();
		public List<Packet>getQueuedPackets();
		public void writePacket(Packet packet) throws IOException;
	}
	
	private Client owner;
	private OutputStream out;
	private DataOutput dos;
	
	private ArrayList<Packet> queuedPackets = new ArrayList<>();
	private ThreadRunner writerThread;
	private boolean active = true;
	
	private Unsave unsave = new Unsave() {
		@Override
		public ThreadRunner getThread() {
			return writerThread;
		}
		
		@Override
		public List<Packet> getQueuedPackets() {
			return queuedPackets;
		}
		
		@Override
		public OutputStream getOutputStream() {
			return out;
		}

		@Override
		public void writePacket(Packet packet) throws IOException {
			write(packet);
		}
	};
	
	public Unsave unsave(){
		return unsave;
	}
	
	public SocketWriter(Client owner, OutputStream os) {
		this.owner = owner;
		this.out = os;
		this.dos = new DataOutputStream(out);
		this.writerThread = ThreadFactory.getFactory().createThread(()->{
			while (active) {
				Packet packet = null;
				synchronized (queuedPackets) {
					if(queuedPackets.size() > 0){
						packet = queuedPackets.get(0);
						queuedPackets.remove(0);
					}
				}
				if(packet != null){
					try{
						write(packet);
					}catch(Exception e){
						handleException(e);
					}
				} else
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		});
		this.writerThread.start();
	}
	
	public void addPacket(Packet packet){
		synchronized (queuedPackets) {
			queuedPackets.add(packet);
		}
	}
	
	private void handleException(Exception e){
		if (e.getMessage().equalsIgnoreCase("Broken pipe") || e.getMessage().equalsIgnoreCase("Connection reset"))
			return;
		if (e.getMessage().equalsIgnoreCase("Socket closed") || e.getMessage().equalsIgnoreCase("Datenübergabe unterbrochen (broken pipe)")) {
			owner.closePipeline(true);
			return;
		}
		e.printStackTrace();
	}
	
	private void write(Packet packet) throws IOException {
		long start = System.currentTimeMillis();
		int id = Packet.getPacketId(packet,PacketDirection.TO_SERVER);
		if (id == -1) {
			System.out.println("Cant find Packet: " + packet);
			return;
		}

		DataBuffer dbuffer = new DataBuffer();
		dbuffer.writeInt(id);
		dbuffer.writeUUID(packet.getPacketUUID());
		packet.write(dbuffer);
		dbuffer.resetReaderIndex();

		dos.writeInt(-dbuffer.writerIndex());
		dos.writeInt(dbuffer.writerIndex());
		dos.write(dbuffer.array(),0,dbuffer.writerIndex());
		out.flush();
		
		long end = System.currentTimeMillis();
		Debugger.debug("Write packet "+packet.getClass().getName()+" in "+(end-start)+" ms");
	}
	
	public void close() {
		active = false;
		try{
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

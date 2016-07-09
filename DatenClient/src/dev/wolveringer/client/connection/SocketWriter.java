package dev.wolveringer.client.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import dev.wolveringer.client.threadfactory.ThreadFactory;
import dev.wolveringer.client.threadfactory.ThreadRunner;
import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.Packet.PacketDirection;

public class SocketWriter {
	public static interface Unsave {
		public OutputStream getOutputStream();
		public ThreadRunner getThread();
		public List<Packet>getQueuedPackets();
		public void writePacket(Packet packet) throws IOException;
	}
	
	private Client owner;
	private OutputStream out;
	private int write;
	private long lastWrite = -1;
	
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
						System.out.println("Writepacket: "+packet.getClass().getName());
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

		if(lastWrite+1000>System.currentTimeMillis()){
			if(write > 100){
				System.err.println("Writing more than 100 packets/sec! (Curruntly: "+write+")");
			}
			lastWrite = System.currentTimeMillis();
			write = 0;
		}
		
		write++;
		DataBuffer dbuffer = new DataBuffer();
		dbuffer.writeInt(id);
		dbuffer.writeUUID(packet.getPacketUUID());
		packet.write(dbuffer);
		dbuffer.resetReaderIndex();

		ByteArrayOutputStream os = new ByteArrayOutputStream(4 + dbuffer.writerIndex()); // [INT(Length)|4][DATA|~]
		os.write(new byte[] { (byte) (dbuffer.writerIndex() >>> 24), (byte) (dbuffer.writerIndex() >>> 16), (byte) (dbuffer.writerIndex() >>> 8), (byte) dbuffer.writerIndex() });
		os.write(dbuffer.array(),0,dbuffer.writerIndex());
		out.write(os.toByteArray());
		out.flush();
		long end = System.currentTimeMillis();
		//System.out.println("Write packet in "+(end-start)+" ms");
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

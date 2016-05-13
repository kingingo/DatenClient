package dev.wolveringer.client.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.Packet.PacketDirection;

public class SocketWriter {
	private Client owner;
	private OutputStream out;
	private int write;
	private long lastWrite = -1;
	public SocketWriter(Client owner, OutputStream os) {
		this.owner = owner;
		this.out = os;
	}

	public synchronized void write(Packet packet) throws IOException {
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
	}
	
	public void close() {
		try{
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

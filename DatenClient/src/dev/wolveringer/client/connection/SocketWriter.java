package dev.wolveringer.client.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import dev.wolveringer.dataclient.protocoll.packets.Packet;

public class SocketWriter {
	private Client owner;
	private OutputStream out;

	public SocketWriter(Client owner, OutputStream os) {
		this.owner = owner;
		this.out = os;
	}

	public void write(Packet packet) throws IOException {
		int id = Packet.getPacketId(packet);

		if (id == -1) {
			System.out.println("Cant find Packet: " + packet);
			return;
		}

		DataBuffer dbuffer = new DataBuffer();
		dbuffer.writeInt(id);
		dbuffer.writeUUID(packet.getPacketUUID());
		packet.write(dbuffer);

		ByteArrayOutputStream os = new ByteArrayOutputStream(4 + dbuffer.writerIndex()); // [INT(Length)|4][DATA|~]
		os.write(new byte[] { (byte) (dbuffer.readableBytes() >>> 24), (byte) (dbuffer.readableBytes() >>> 16), (byte) (dbuffer.readableBytes() >>> 8), (byte) dbuffer.readableBytes() });
		os.write(dbuffer.array());
		
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

package dev.wolveringer.client.connection;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;

public class Client {
	private Socket socket;
	private ReaderThread reader;
	private SocketWriter writer;
	private PacketHandlerBoss boss;
	@Getter
	protected ClientType type;
	@Getter
	protected String host;
	@Getter
	protected String name;
	
	public Client(Socket socket) {
		this.socket = socket;
		try{
			this.writer = new SocketWriter(this, socket.getOutputStream());
			this.reader = new ReaderThread(this, socket.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		this.boss = new PacketHandlerBoss(this);
		this.reader.start();
	}	
	
	public void disconnect(){
		disconnect(null);
	}
	
	public void disconnect(String message){
		writePacket(new PacketOutDisconnect(message));
	}
	
	protected void closePipeline(){
		reader.close();
		writer.close();
	}
	
	public void writePacket(Packet packet){
		try {
			writer.write(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected PacketHandlerBoss getHandlerBoss(){
		return boss;
	}
}

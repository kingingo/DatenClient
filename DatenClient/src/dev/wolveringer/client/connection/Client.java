package dev.wolveringer.client.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketDisconnect;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutHandschakeStart;
import lombok.Getter;

public class Client {
	private InetSocketAddress target;
	@Getter
	protected ClientType type;
	
	private Socket socket;
	private ReaderThread reader;
	private SocketWriter writer;
	private PacketHandlerBoss boss;

	protected String host = "underknown";
	protected String name;
	
	private byte[] password;
	
	private int timeout = 5000;
	
	public Client(InetSocketAddress target,ClientType type,String clientName) {
		this.target = target;
		this.type = type;
		this.name = clientName;
	}	
	
	public void connect(byte[] password){
		this.password = password;
		try{
			socket = new Socket(target.getAddress(),target.getPort());
			this.writer = new SocketWriter(this, socket.getOutputStream());
			this.reader = new ReaderThread(this, socket.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		this.boss = new PacketHandlerBoss(this);
		this.reader.start();
		
		//Handschaking
		writePacket(new PacketOutHandschakeStart(host, name, password, type));
		
		long start = System.currentTimeMillis();
		while (!boss.handschakeComplete) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(start+timeout<System.currentTimeMillis())
				throw new RuntimeException("Handschke needs longer than 5000ms");
		}
		System.out.println("Connected");
	}
	
	public void disconnect(){
		disconnect(null);
	}
	
	public void disconnect(String message){
		writePacket(new PacketDisconnect(message));
	}
	
	protected void closePipeline(){
		reader.close();
		writer.close();
		System.out.println("Client disconnected.");
	}
	
	public void writePacket(Packet packet){
		try {
			writer.write(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public PacketHandlerBoss getHandlerBoss(){
		return boss;
	}
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Starting test Client");
		Client client = new Client(new InetSocketAddress("localhost", 1111), ClientType.BUNGEECORD, "01");
		client.connect("HelloWorld".getBytes());
		while (true) {
			Thread.sleep(10000);
		}
	}
}

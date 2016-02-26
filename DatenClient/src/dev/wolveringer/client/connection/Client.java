package dev.wolveringer.client.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import dev.wolveringer.client.external.ActionListener;
import dev.wolveringer.client.external.BungeeCordActionListener;
import dev.wolveringer.client.external.ServerActionListener;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketDisconnect;
import dev.wolveringer.dataclient.protocoll.packets.PacketOutHandschakeStart;
import lombok.Getter;

public class Client {
	public static Client createBungeecordClient(String name,InetSocketAddress target,BungeeCordActionListener listener,ServerInformations infos){
		Client client = new Client(target, ClientType.BUNGEECORD, name);
		client.externalHandler = listener;
		client.infoSender = new ServerStatusSender(client, infos);
		return client;
	}
	public static Client createServerClient(ClientType type,String name,InetSocketAddress target,ServerActionListener listener,ServerInformations infos){
		if(type == ClientType.BUNGEECORD)
			throw new RuntimeException();
		Client client = new Client(target, type, name);
		client.infoSender = new ServerStatusSender(client, infos);
		client.externalHandler = listener;
		return client;
	}
	
	private InetSocketAddress target;
	@Getter
	protected ClientType type;
	
	protected Socket socket;
	private ReaderThread reader;
	private SocketWriter writer;
	private PacketHandlerBoss boss;

	protected String host = "underknown";
	@Getter
	protected String name;
	
	private byte[] password;
	
	private int timeout = 5000;
	
	private ActionListener externalHandler;
	
	protected long lastPingTime = -1;
	protected long lastPing = -1;
	private TimeOutThread timeOut;
	
	protected boolean connected = false;
	
	protected ServerStatusSender infoSender;
	
	Client(InetSocketAddress target,ClientType type,String clientName) {
		this.target = target;
		this.type = type;
		this.name = clientName;
		this.timeOut = new TimeOutThread(this);
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
		connected = true;
		this.boss = new PacketHandlerBoss(this);
		this.reader.start();
		//Handschaking
		writePacket(new PacketOutHandschakeStart(host, name, password, type));
		
		long start = System.currentTimeMillis();
		while (!boss.handschakeComplete) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			if(start+timeout<System.currentTimeMillis())
				throw new RuntimeException("Handschke needs longer than 5000ms");
		}
		timeOut.start();
		infoSender.start();
	}
	
	public void disconnect(){
		disconnect(null);
	}
	
	public void disconnect(String message){
		writePacket(new PacketDisconnect(message));
		closePipeline();
	}
	
	protected void closePipeline(){
		if(!connected)
			return;
		connected = false;
		reader.close();
		writer.close();
		timeOut.stop();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		externalHandler.disconnected();
	}
	
	public void writePacket(Packet packet){
		try {
			writer.write(packet);
		} catch (IOException e) {
			if(e.getMessage().equalsIgnoreCase("Broken pipe") || e.getMessage().equalsIgnoreCase("Connection reset"))
				return;
			if(e.getMessage().equalsIgnoreCase("Socket closed")){
				connected = false;
				reader.close();
				writer.close();
				return;
			}
			e.printStackTrace();
		}
	}
	public PacketHandlerBoss getHandlerBoss(){
		return boss;
	}
	protected ActionListener getExternalHandler() {
		return externalHandler;
	}
	public long getPing() {
		return lastPing;
	}
	public boolean isConnected() {
		return connected;
	}
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Starting test Client");
		Client client = new Client(new InetSocketAddress("localhost", 1111), ClientType.BUNGEECORD, "01");
		client.connect("HelloWorld".getBytes());
		while (true) {
			Thread.sleep(10000);
		}
	}
	public void updateServerStats() {
		infoSender.updateServerStats();
	}
}

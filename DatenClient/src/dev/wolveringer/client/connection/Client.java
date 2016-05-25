package dev.wolveringer.client.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.ProgressFuture;
import dev.wolveringer.client.external.ActionListener;
import dev.wolveringer.client.external.BungeeCordActionListener;
import dev.wolveringer.client.external.ServerActionListener;
import dev.wolveringer.client.futures.StatusResponseFuture;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketDisconnect;
import dev.wolveringer.dataserver.protocoll.packets.PacketHandschakeInStart;
import dev.wolveringer.dataserver.protocoll.packets.PacketInServerStatus;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus.Error;
import dev.wolveringer.event.EventManager;
import lombok.Getter;

public class Client {
	public static Client createBungeecordClient(String name, InetSocketAddress target, BungeeCordActionListener listener, ServerInformations infos) {
		Client client = new Client(target, ClientType.BUNGEECORD, name, infos);
		client.externalHandler = listener;
		return client;
	}

	public static Client createServerClient(ClientType type, String name, InetSocketAddress target, ServerActionListener listener, ServerInformations infos) {
		if (type == ClientType.BUNGEECORD)
			throw new RuntimeException();
		Client client = new Client(target, type, name, infos);
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

	protected String host = "unknown";
	@Getter
	protected String name;

	private int timeout = 5000;

	private ActionListener externalHandler;

	protected long lastPingTime = -1;
	protected long lastPing = -1;
	private TimeOutThread timeOut;

	protected boolean connected = false;

	@Getter
	protected ServerStatusSender infoSender;
	private ServerInformations infoHandler;

	@Getter
	private EventManager eventManager;

	private Client(InetSocketAddress target, ClientType type, String clientName, ServerInformations infoHandler) {
		this.target = target;
		this.type = type;
		this.name = clientName;
		this.infoHandler = infoHandler;
		this.eventManager = new EventManager(this);
	}

	public void connect(byte[] password) throws Exception {
		if (isConnected())
			throw new RuntimeException("Client alredy connected!");
		if(this.boss == null)
			this.boss = new PacketHandlerBoss(this);
		this.socket = new Socket(target.getAddress(), target.getPort());
		this.writer = new SocketWriter(this, socket.getOutputStream());
		this.reader = new ReaderThread(this, socket.getInputStream());
		this.timeOut = new TimeOutThread(this);
		this.infoSender = new ServerStatusSender(this, infoHandler);
		this.reader.start();
		connected = true;
		//Handschaking
		writePacket(new PacketHandschakeInStart(host, name, password, type, Packet.PROTOCOLL_VERSION));
		long start = System.currentTimeMillis();
		while (!boss.handschakeComplete) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			if(boss.handschakeErrors != null){
				disconnect();
				throw new RuntimeException("Errors happend while handschaking: \n"+StringUtils.join(boss.handschakeErrors,"\n -"));
			}
			if(boss.handschakeDisconnect != null){
				disconnect();
				throw new RuntimeException("Server denied connection. Reson: "+boss.handschakeDisconnect);
			}
			if (start + timeout < System.currentTimeMillis()){
				disconnect();
				throw new RuntimeException("Handshake needs longer than 5000ms");
			}
		}
		ClientWrapper.unloadAllPlayers();
		timeOut.start();
		infoSender.start();
		eventManager.updateAll();
	}

	public void disconnect() {
		disconnect(null);
	}

	public void disconnect(String message) {
		writePacket(new PacketDisconnect(message));
		closePipeline();
	}

	protected void closePipeline() {
		if (!connected)
			return;
		connected = false;
		reader.close();
		writer.close();
		timeOut.stop();
		infoSender.stop();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		externalHandler.disconnected();
		socket = null;
		reader = null;
		writer = null;
		timeOut = null;
		infoSender = null;
		lastPingTime = -1;
		lastPing = -1;
		//boss.handschakeComplete = false;
		boss.reset();
	}

	public ProgressFuture<Error[]> writePacket(Packet packet) {
		StatusResponseFuture f = new StatusResponseFuture(this, packet.getPacketUUID());
		writePacket0(packet);
		return f;

	}

	private synchronized void writePacket0(Packet packet) {
		try {
			writer.write(packet);
		} catch (IOException e) {
			if (e.getMessage().equalsIgnoreCase("Broken pipe") || e.getMessage().equalsIgnoreCase("Connection reset"))
				return;
			if (e.getMessage().equalsIgnoreCase("Socket closed")) {
				connected = false;
				reader.close();
				writer.close();
				return;
			}
			e.printStackTrace();
		}
	}

	public PacketHandlerBoss getHandlerBoss() {
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
		Client client = new Client(new InetSocketAddress("localhost", 1111), ClientType.BUNGEECORD, "01", new ServerInformations() {
			@Override
			public PacketInServerStatus getStatus() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		try {
			client.connect("HelloWorld".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (true) {
			Thread.sleep(10000);
		}
	}

	public void updateServerStats() {
		infoSender.updateServerStats();
	}
	
	public boolean isHandschakeCompleded(){
		return boss == null ? false : boss.handschakeComplete;
	}
}

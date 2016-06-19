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
import dev.wolveringer.dataserver.protocoll.packets.PacketHandshakeInStart;
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

	protected String host = "underknown";
	@Getter
	protected String name;

	private int timeout = 5000;

	private ActionListener externalHandler;

	private PingManager ping;
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
		try {
			if (isConnected())
				throw new RuntimeException("Client already connected!");
			if (this.boss == null)
				this.boss = new PacketHandlerBoss(this);
			this.socket = new Socket(target.getAddress(), target.getPort());
			this.writer = new SocketWriter(this, socket.getOutputStream());
			this.reader = new ReaderThread(this, socket.getInputStream());
			this.ping = new PingManager(this);
			this.timeOut = new TimeOutThread(this);
			this.infoSender = new ServerStatusSender(this, infoHandler);
			this.reader.start();
			connected = true;
			//Handschaking
			writePacket(new PacketHandshakeInStart(host, name, password, type, Packet.PROTOCOLL_VERSION));
			long start = System.currentTimeMillis();
			while (!boss.handshakeComplete) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
				if (boss.handshakeErrors != null) {
					disconnect();
					throw new RuntimeException("Errors happend while handshaking: \n" + StringUtils.join(boss.handshakeErrors, "\n -"));
				}
				if (boss.handshakeDisconnect != null) {
					disconnect();
					throw new RuntimeException("Server denied connection. Reason: " + boss.handshakeDisconnect);
				}
				if (start + timeout < System.currentTimeMillis()) {
					disconnect();
					throw new RuntimeException("Handschake needs longer than 5000ms");
				}
			}
			ClientWrapper.unloadAllPlayers();
			timeOut.start();
			infoSender.start();
			eventManager.updateAll();
			getExternalHandler().connected();
		} catch (Exception e) {
			closePipeline(true);
			throw e;
		}
	}

	public void disconnect() {
		disconnect(null);
	}

	public void disconnect(String message) {
		try{
			writePacket(new PacketDisconnect(message));
		}catch(Exception e){
			e.printStackTrace();
		}
		closePipeline(false);
	}

	protected void closePipeline(boolean force) {
		if (!connected && !force)
			return;
		connected = false;
		if (reader != null)
			reader.close();
		if (writer != null)
			writer.close();
		if (timeOut != null)
			timeOut.stop();
		if (infoSender != null)
			infoSender.stop();
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (externalHandler != null)
			externalHandler.disconnected();
		socket = null;
		reader = null;
		writer = null;
		timeOut = null;
		infoSender = null;
		ping = null;
		if (boss != null)
			boss.reset();
	}

	public ProgressFuture<Error[]> writePacket(Packet packet) {
		StatusResponseFuture f = new StatusResponseFuture(this, packet.getPacketUUID());
		writePacket0(packet);
		return f;

	}

	private synchronized void writePacket0(Packet packet) {
		if(writer == null)
			return;
		try {
			writer.write(packet);
		} catch (IOException e) {
			if (e.getMessage().equalsIgnoreCase("Broken pipe") || e.getMessage().equalsIgnoreCase("Connection reset"))
				return;
			if (e.getMessage().equalsIgnoreCase("Socket closed") || e.getMessage().equalsIgnoreCase("Datenübergabe unterbrochen (broken pipe)")) {
				connected = false;
				if (reader != null)
					reader.close();
				if (writer != null)
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

	public PingManager getPingManager() {
		return ping;
	}

	public boolean isConnected() {
		return connected;
	}
	
	public void updateServerStats() {
		infoSender.updateServerStats();
	}

	public boolean isHandshakeCompleted() {
		return boss == null ? false : boss.handshakeComplete;
	}
}

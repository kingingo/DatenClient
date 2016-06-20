package dev.wolveringer.client.connection;

import java.util.ArrayList;
import java.util.List;

import dev.wolveringer.client.external.BungeeCordActionListener;
import dev.wolveringer.client.external.ServerActionListener;
import dev.wolveringer.client.futures.PacketResponseFuture;
import dev.wolveringer.dataserver.protocoll.DataBuffer;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketChatMessage;
import dev.wolveringer.dataserver.protocoll.packets.PacketChatMessage.Target;
import dev.wolveringer.dataserver.protocoll.packets.PacketDisconnect;
import dev.wolveringer.dataserver.protocoll.packets.PacketEventFire;
import dev.wolveringer.dataserver.protocoll.packets.PacketForward;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutGammodeChange;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutHandschakeAccept;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus.Error;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPlayerSettings;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPlayerSettings.SettingValue;
import dev.wolveringer.dataserver.protocoll.packets.PacketPing;
import dev.wolveringer.dataserver.protocoll.packets.PacketPong;
import dev.wolveringer.dataserver.protocoll.packets.PacketServerAction;
import dev.wolveringer.dataserver.protocoll.packets.PacketServerAction.PlayerAction;
import dev.wolveringer.event.EventListener;
import dev.wolveringer.dataserver.protocoll.packets.PacketServerMessage;
import dev.wolveringer.dataserver.protocoll.packets.PacketSettingUpdate;

public class PacketHandlerBoss {
	private List<PacketListener> listener = new ArrayList<>();
	
	private boolean debug = false;
	
	private Client owner;
	protected boolean handshakeComplete = false;
	protected Error[] handshakeErrors = null;
	protected String handshakeDisconnect = null;
	
	public PacketHandlerBoss(Client owner) {
		this.owner = owner;
	}

	public void addListener(PacketListener listener){
		if(listener == null)
			throw new NullPointerException("Listener cant be null!");
		this.listener.add(listener);
	}
	public void removeListener(PacketListener listener){
		this.listener.remove(listener);
	}
	
	protected void reset(){
		handshakeComplete = false;
		handshakeErrors = null;
		handshakeDisconnect = null;
		for(PacketListener l : new ArrayList<>(listener))
			if(l instanceof PacketResponseFuture)
				listener.remove(l);
	}
	
	protected void handle(Packet packet) {
		if(packet instanceof PacketOutHandschakeAccept){
			handshakeComplete = true;
		}
		if(packet instanceof PacketOutPacketStatus){
			if(handshakeComplete == false){
				handshakeErrors = ((PacketOutPacketStatus) packet).getErrors();
			}
			if(((PacketOutPacketStatus)packet).getErrors().length == 0){
				if(debug)
					System.out.println("Packet sucessfull handled ("+((PacketOutPacketStatus)packet).getPacketId()+")");
			}
			else
			{
				if(debug){
					System.out.println("Error Packet ("+packet.getPacketUUID()+") -> Errors:");
					for(PacketOutPacketStatus.Error r : ((PacketOutPacketStatus)packet).getErrors())
						System.out.println(" - "+r.getMessage());
				}
			}
			//owner.closePipeline();
		}
		if(packet instanceof PacketDisconnect){
			if(debug)
				System.out.println("Disconnected: "+((PacketDisconnect)packet).getReson());
			if(handshakeComplete == false){
				handshakeDisconnect = ((PacketDisconnect)packet).getReson();
			}
			owner.closePipeline(false);
			owner.getExternalHandler().disconnected();
		}
		if(packet instanceof PacketOutPlayerSettings){
			if(debug){
				System.out.println("Player settings for: "+((PacketOutPlayerSettings)packet).getPlayer());
				for(SettingValue s : ((PacketOutPlayerSettings) packet).getValues())
					System.out.println("   "+s.getSetting()+" -> "+s.getValue());
			}
		}
		if(packet instanceof PacketServerAction){
			for(PlayerAction a : ((PacketServerAction) packet).getActions()){
				switch (a.getAction()) {
				case KICK:
					owner.getExternalHandler().kickPlayer(a.getPlayer(),a.getValue());
					break;
				case SEND:
					if(!(owner.getExternalHandler() instanceof BungeeCordActionListener))
						System.out.println("Player sending not supported");
					else
						((BungeeCordActionListener)owner.getExternalHandler()).sendPlayer(a.getPlayer(), a.getValue());
					break;
				case RESTART:
					owner.getExternalHandler().restart(a.getValue());
					break;
				case STOP:
					owner.getExternalHandler().stop(a.getValue());
					break;
				default:
					break;
				}
			}
		}
		else if(packet instanceof PacketOutGammodeChange){
			if(!(owner.getExternalHandler() instanceof ServerActionListener))
				System.out.println("Gammodechange not supported");
			else{
				((ServerActionListener)owner.getExternalHandler()).setGamemode(((PacketOutGammodeChange) packet).getGame(),((PacketOutGammodeChange) packet).getSubType());
			}
		}
		else if(packet instanceof PacketChatMessage){
			for(Target t : ((PacketChatMessage) packet).getTargets()){
				switch (t.getType()) {
				case BROTCAST:
					owner.getExternalHandler().broadcast(t.getPermission(), ((PacketChatMessage) packet).getMessage());
					break;
				case PLAYER:
					owner.getExternalHandler().sendMessage(Integer.parseInt(t.getTarget()),((PacketChatMessage) packet).getMessage());
				default:
					break;
				}
			}
		}
		else if(packet instanceof PacketPong){
			owner.getPingManager().handlePong((PacketPong) packet);
		}
		else if(packet instanceof PacketPing){
			owner.writePacket(new PacketPong(System.currentTimeMillis()));
		}
		else if(packet instanceof PacketServerMessage){
			DataBuffer buffer;
			owner.getExternalHandler().serverMessage(((PacketServerMessage) packet).getChannel(), buffer = new DataBuffer(((PacketServerMessage) packet).getMessage()));
			buffer.release();
		}
		else if(packet instanceof PacketSettingUpdate){
			owner.getExternalHandler().settingUpdate(((PacketSettingUpdate) packet).getPlayer(), ((PacketSettingUpdate) packet).getSetting(), ((PacketSettingUpdate) packet).getValue());
		}
		else if(packet instanceof PacketEventFire){
			for(EventListener l : owner.getEventManager().getListener())
				l.fireEvent(((PacketEventFire) packet).getEvent());
		}
		if(packet instanceof PacketForward){
			Packet pack = ((PacketForward) packet).getPacket();
			if(pack != null)
				for(PacketListener l : new ArrayList<>(listener))
					if(l != null)
						l.handle(pack);
					else
						listener.remove(null);
		}
		else
			for(PacketListener l : new ArrayList<>(listener))
				if(l != null)
					l.handle(packet);
		if(!handshakeComplete)
			return;
	}

}

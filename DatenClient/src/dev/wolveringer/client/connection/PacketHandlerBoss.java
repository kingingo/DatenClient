package dev.wolveringer.client.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.wolveringer.client.external.BungeeCordActionListener;
import dev.wolveringer.client.external.ServerActionListener;
import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketChatMessage;
import dev.wolveringer.dataclient.protocoll.packets.PacketChatMessage.Target;
import dev.wolveringer.dataclient.protocoll.packets.PacketDisconnect;
import dev.wolveringer.dataclient.protocoll.packets.PacketInGammodeChange;
import dev.wolveringer.dataclient.protocoll.packets.PacketInHandschakeAccept;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPacketStatus;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPlayerSettings;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse;
import dev.wolveringer.dataclient.protocoll.packets.PacketPingPong;
import dev.wolveringer.dataclient.protocoll.packets.PacketServerAction;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPlayerSettings.SettingValue;
import dev.wolveringer.dataclient.protocoll.packets.PacketInUUIDResponse.UUIDKey;
import dev.wolveringer.dataclient.protocoll.packets.PacketServerAction.PlayerAction;

public class PacketHandlerBoss {
	private List<PacketListener> listener = new ArrayList<>();
	
	private boolean debug = false;
	
	private Client owner;
	protected boolean handschakeComplete = false;

	public PacketHandlerBoss(Client owner) {
		this.owner = owner;
	}

	public void addListener(PacketListener listener){
		this.listener.add(listener);
	}
	public void removeListener(PacketListener listener){
		this.listener.remove(listener);
	}
	
	protected void handle(Packet packet) {
		if(packet instanceof PacketInHandschakeAccept){
			handschakeComplete = true;
			System.out.println("Connected to Server");
			//owner.writePacket(new PacketOutConnectionStatus("WolverinDEV", Status.CONNECTED));
			/*
			UUID wolverindev = UUID.fromString("a8de450b-6853-3d1a-88ed-72bc2f08dfcd"); //Offline: a8de450b-6853-3d1a-88ed-72bc2f08dfcd Online: 57091d6f-839f-48b7-a4b1-4474222d4ad1
			owner.writePacket(new PacketOutUUIDRequest(new String[]{"WolverinDEV"}));
			//owner.writePacket(new PacketOutPlayerSettingsRequest(wolverindev, new Setting[]{Setting.PREMIUM_LOGIN,Setting.UUID}));
			
			//owner.writePacket(new PacketOutChangePlayerSettings(wolverindev, Setting.PREMIUM_LOGIN, true+""));
			owner.writePacket(new PacketOutUUIDRequest(new String[]{"WolverinDEV"}));
			//owner.writePacket(new PacketOutPlayerSettingsRequest(wolverindev, new Setting[]{Setting.PREMIUM_LOGIN,Setting.UUID}));
			//owner.writePacket(new PacketOutPlayerSettingsRequest(wolverindev, new Setting[]{Setting.PREMIUM_LOGIN,Setting.UUID}));
			*/
		}
		else if(packet instanceof PacketInPacketStatus){
			if(((PacketInPacketStatus)packet).getErrors().length == 0){
				if(debug)
					System.out.println("Packet sucessfull handled ("+((PacketInPacketStatus)packet).getPacketId()+")");
			}
			else
			{
				if(debug){
					System.out.println("Error Packet ("+packet.getPacketUUID()+") -> Errors:");
					for(PacketInPacketStatus.Error r : ((PacketInPacketStatus)packet).getErrors())
						System.out.println(" - "+r.getMessage());
				}
			}
			//owner.closePipeline();
		}
		else if(packet instanceof PacketDisconnect){
			if(debug)
				System.out.println("Disconnected: "+((PacketDisconnect)packet).getReson());
			owner.closePipeline();
		}
		else if(packet instanceof PacketInPlayerSettings){
			if(debug){
				System.out.println("Player settings for: "+((PacketInPlayerSettings)packet).getPlayer());
				for(SettingValue s : ((PacketInPlayerSettings) packet).getValues())
					System.out.println("   "+s.getSetting()+" -> "+s.getValue());
			}
		}
		else if(packet instanceof PacketInUUIDResponse){
			if(debug){
				System.out.println("UUID response");
				for(UUIDKey k : ((PacketInUUIDResponse)packet).getUuids())
					System.out.println(k.getName()+" - "+k.getUuid());
			}
		}
		else
			if(debug)
				System.out.println("Handle: "+packet);
		if(packet instanceof PacketServerAction){
			System.out.println("Player server action not implimted yet!");
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
				default:
					break;
				}
			}
		}
		else if(packet instanceof PacketInGammodeChange){
			if(!(owner.getExternalHandler() instanceof ServerActionListener))
				System.out.println("Gammodechange not supported");
			else{
				((ServerActionListener)owner.getExternalHandler()).setGamemode(((PacketInGammodeChange) packet).getGame());
			}
		}
		else if(packet instanceof PacketChatMessage){
			for(Target t : ((PacketChatMessage) packet).getTargets()){
				switch (t.getType()) {
				case BROTCAST:
					owner.getExternalHandler().brotcast(t.getPermission(), ((PacketChatMessage) packet).getMessage());
					break;
				case PLAYER:
					owner.getExternalHandler().sendMessage(UUID.fromString(t.getTarget()),((PacketChatMessage) packet).getMessage());
				default:
					break;
				}
			}
		}
		else if(packet instanceof PacketPingPong){
			owner.lastPing = System.currentTimeMillis()-((PacketPingPong)packet).getTime();
			owner.lastPingTime = System.currentTimeMillis();
		}
		for(PacketListener l : new ArrayList<>(listener))
			l.handle(packet);
		if(!handschakeComplete)
			return;
	}

}

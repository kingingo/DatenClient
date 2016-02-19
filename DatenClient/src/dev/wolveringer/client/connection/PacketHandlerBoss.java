package dev.wolveringer.client.connection;

import dev.wolveringer.dataclient.protocoll.packets.Packet;
import dev.wolveringer.dataclient.protocoll.packets.PacketDisconnect;
import dev.wolveringer.dataclient.protocoll.packets.PacketInHandschakeAccept;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPacketStatus;

public class PacketHandlerBoss {
	private Client owner;
	private boolean handschakeComplete = false;

	public PacketHandlerBoss(Client owner) {
		this.owner = owner;
	}

	public void handle(Packet packet) {
		if(packet instanceof PacketInHandschakeAccept){
			handschakeComplete = true;
			System.out.println("Connected to Server");
		}
		else if(packet instanceof PacketInPacketStatus){
			System.out.println("Error Packet -> Errors:");
			for(PacketInPacketStatus.Error r : ((PacketInPacketStatus)packet).getErrors())
				System.out.println(" - "+r.getMessage());
			//owner.closePipeline();
		}
		else if(packet instanceof PacketDisconnect){
			System.out.println("Disconnected: "+((PacketDisconnect)packet).getReson());
			owner.closePipeline();
		}
		if(!handschakeComplete)
			return;
	}

}

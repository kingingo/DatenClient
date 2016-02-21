package dev.wolveringer.client;

import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.packets.PacketInPacketStatus;
import dev.wolveringer.dataclient.protocoll.packets.PacketInPacketStatus.Error;

@SuppressWarnings("serial")
public class PacketHandleErrorException extends RuntimeException{
	private PacketInPacketStatus packet;
	
	public PacketHandleErrorException(PacketInPacketStatus packet) {
		super(packet.getErrors().length+" errors incurred in this action");
		this.packet = packet;
	}
	
	public Error[] getErrors(){
		return packet.getErrors();
	}
	public UUID getHandleUUID(){
		return packet.getPacketId();
	}
}

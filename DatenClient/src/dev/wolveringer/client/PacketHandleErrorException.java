package dev.wolveringer.client;

import java.util.Arrays;
import java.util.UUID;

import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus.Error;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;

@SuppressWarnings("serial")
public class PacketHandleErrorException extends RuntimeException{
	private PacketOutPacketStatus packet;
	
	public PacketHandleErrorException(PacketOutPacketStatus packet) {
		super(packet.getErrors().length+" errors incurred in this action "+Arrays.deepToString(packet.getErrors())+"");
		this.packet = packet;
	}
	
	public Error[] getErrors(){
		return packet.getErrors();
	}
	public UUID getHandleUUID(){
		return packet.getPacketId();
	}
}

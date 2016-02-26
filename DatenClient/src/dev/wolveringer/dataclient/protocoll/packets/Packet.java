package dev.wolveringer.dataclient.protocoll.packets;

import java.lang.reflect.Constructor;
import java.util.UUID;

import dev.wolveringer.dataclient.protocoll.DataBuffer;
import lombok.Getter;

public abstract class Packet {
	private static final boolean SERVER = false;
	public static enum PacketDirection {
		TO_CLIENT,
		TO_SERVER;
		private PacketDirection() {}
	}
	
	private static Constructor<? extends Packet> inPackets[] = new Constructor[256];
	private static Constructor<? extends Packet> outPackets[] = new Constructor[256];
	
	public static Packet createPacket(int id, DataBuffer buffer) {
		try {
			Constructor<? extends Packet> c = inPackets[id];
			if (c != null) {
				Packet packet = c.newInstance();
				packet.packetUUID = buffer.readUUID();
				packet.read(buffer);
				return packet;
			} else
				System.out.println("Packet 0x" + (Integer.toHexString(id).toUpperCase()) + " not found");
		} catch (Exception e) {
			System.err.println("Exception Packet class: "+inPackets[id].getDeclaringClass());
			e.printStackTrace();
		}
		return null;
	}

	public static int getPacketId(Packet packet) {
		int i = 0;
		for (Constructor<?> c : outPackets) {
			if (c != null){
				if (c.getDeclaringClass().equals(packet.getClass()))
					return i;
			}
			i++;
		}
		return -1;
	}
	
	public static void registerPacket(int id,Class<? extends Packet> packet,PacketDirection direction){
		try {
			if(direction == PacketDirection.TO_SERVER)
				outPackets[id] = packet.getConstructors().length == 1 ? (Constructor<? extends Packet>) packet.getConstructors()[0] : packet.getConstructor();
			else
				inPackets[id] = packet.getConstructors().length == 1 ? (Constructor<? extends Packet>) packet.getConstructors()[0] : packet.getConstructor();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	static {
		registerPacket(0xFF, PacketDisconnect.class, PacketDirection.TO_CLIENT);
		registerPacket(0xFF, PacketDisconnect.class, PacketDirection.TO_SERVER);
		
		registerPacket(0x00, PacketOutHandschakeStart.class, PacketDirection.TO_SERVER);
		
		registerPacket(0x01, PacketOutBanStatsRequest.class, PacketDirection.TO_SERVER);
		registerPacket(0x02, PacketOutChangePlayerSettings.class, PacketDirection.TO_SERVER);
		registerPacket(0x03, PacketOutPlayerSettingsRequest.class, PacketDirection.TO_SERVER);
		registerPacket(0x04, PacketOutConnectionStatus.class,  PacketDirection.TO_SERVER);
		registerPacket(0x05, PacketOutServerSwitch.class, PacketDirection.TO_SERVER);
		registerPacket(0x06, PacketOutStatsEdit.class, PacketDirection.TO_SERVER);
		registerPacket(0x07, PacketOutStatsRequest.class, PacketDirection.TO_SERVER);
		registerPacket(0x08, PacketOutUUIDRequest.class, PacketDirection.TO_SERVER);
		registerPacket(0x09, PacketOutGetServer.class, PacketDirection.TO_SERVER);
		registerPacket(0x0A, PacketOutBanPlayer.class, PacketDirection.TO_SERVER);
		registerPacket(0x0B, PacketOutNameRequest.class, PacketDirection.TO_SERVER);
		registerPacket(0x0C, PacketServerAction.class, PacketDirection.TO_SERVER);
		registerPacket(0x0D, PacketOutServerStatus.class, PacketDirection.TO_SERVER);
		registerPacket(0x0E, PacketOutServerStatusRequest.class, PacketDirection.TO_SERVER);
		registerPacket(0x0F, PacketChatMessage.class, PacketDirection.TO_SERVER);
		registerPacket(0x10, PacketServerMessage.class, PacketDirection.TO_SERVER);
		registerPacket(0x11, PacketForward.class, PacketDirection.TO_SERVER);
		
		registerPacket(0xF0, PacketInPacketStatus.class, PacketDirection.TO_CLIENT);
		registerPacket(0x00, PacketInHandschakeAccept.class, PacketDirection.TO_CLIENT);
		registerPacket(0x01, PacketInStats.class, PacketDirection.TO_CLIENT);
		registerPacket(0x02, PacketInPlayerSettings.class, PacketDirection.TO_CLIENT);
		registerPacket(0x03, PacketInUUIDResponse.class, PacketDirection.TO_CLIENT);
		registerPacket(0x04, PacketInPlayerServer.class, PacketDirection.TO_CLIENT);
		registerPacket(0x05, PacketInBanStats.class, PacketDirection.TO_CLIENT);
		registerPacket(0x06, PacketInNameResponse.class, PacketDirection.TO_CLIENT);
		registerPacket(0x07, PacketServerAction.class, PacketDirection.TO_CLIENT);
		registerPacket(0x08, PacketInServerStatus.class, PacketDirection.TO_CLIENT);
		registerPacket(0x09, PacketChatMessage.class, PacketDirection.TO_CLIENT);
		registerPacket(0x0A, PacketInGammodeChange.class, PacketDirection.TO_CLIENT);
		registerPacket(0x0B, PacketServerMessage.class, PacketDirection.TO_CLIENT);
		registerPacket(0x0C, PacketForward.class, PacketDirection.TO_SERVER);
		
		registerPacket(0xFE, PacketPingPong.class, PacketDirection.TO_CLIENT);
		registerPacket(0xFE, PacketPingPong.class, PacketDirection.TO_SERVER);
	}

	@Getter
	private UUID packetUUID = UUID.randomUUID();
	
	public Packet() {
	}
	
	public void read(DataBuffer buffer){
		throw new NullPointerException("Packet is write only");
	}
	public void write(DataBuffer buffer){
		throw new NullPointerException("Packet is read only");
	}
	//TODO
	//Messages
	//Bann
}

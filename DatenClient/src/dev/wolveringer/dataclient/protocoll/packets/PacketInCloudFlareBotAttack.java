package dev.wolveringer.dataclient.protocoll.packets;

public class PacketInCloudFlareBotAttack extends Packet{
	private boolean attack;
	
	public void read(dev.wolveringer.dataclient.protocoll.DataBuffer buffer) {
		attack = buffer.readBoolean();
	};
	
	public boolean attackActive() {
		return attack;
	}
}

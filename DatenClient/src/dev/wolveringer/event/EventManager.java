package dev.wolveringer.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;
import dev.wolveringer.events.EventConditions;
import dev.wolveringer.events.EventType;
import lombok.Getter;
import lombok.Setter;

public class EventManager {
	private Client handle;
	
	private HashMap<EventType, SpecificEventManager> managers = new HashMap<>();
	private ArrayList<EventListener> listeners  = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean sync = false;
	
	public EventManager(Client client) {
		this.handle = client;
		for(EventType type : EventType.values())
			managers.put(type, new SpecificEventManager(type,this));
	}
	
	public void registerListener(EventListener listener){
		listeners.add(listener);
	}
	public void deregisterListener(EventListener listener){
		listeners.remove(listener);
	}
	
	public List<EventListener> getListener(){
		return Collections.unmodifiableList(new ArrayList<>(listeners));
	}
	
	public SpecificEventManager getEventManager(EventType type){
		return managers.get(type);
	}
	public Client getHandle() {
		return handle;
	}
	
	public void updateAll(){
		for(SpecificEventManager m : managers.values())
			m.updateAll();
	}
	
	protected void writePacket(Packet packet){
		if(sync)
			handle.writePacket(packet).getSync();
		else
			handle.writePacket(packet).getAsync(new Callback<PacketOutPacketStatus.Error[]>() {
				@Override
				public void call(PacketOutPacketStatus.Error[] obj) {
					
				}
			});
	}
	
	public static void main(String[] args) {
		EventManager manger = new EventManager(null);
		manger.getEventManager(EventType.SERVER_SWITCH).setEventEnabled(true);
		manger.getEventManager(EventType.SERVER_SWITCH).setConditionEnables(EventConditions.SERVER_NAME_ARRAY, true);
		manger.getEventManager(EventType.SERVER_SWITCH).getCondition(EventConditions.SERVER_NAME_ARRAY).addValue(" ");
		manger.getEventManager(EventType.SERVER_SWITCH).getCondition(EventConditions.SERVER_NAME_ARRAY).addValue("y");
	}
}

package dev.wolveringer.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import dev.wolveringer.client.Callback;
import dev.wolveringer.client.connection.Client;
import dev.wolveringer.dataserver.protocoll.packets.Packet;
import dev.wolveringer.dataserver.protocoll.packets.PacketOutPacketStatus;
import dev.wolveringer.events.Event;
import dev.wolveringer.events.EventConditions;
import dev.wolveringer.events.EventType;
import dev.wolveringer.thread.ThreadFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class EventManager {
	@AllArgsConstructor
	@Getter
	private static class EventListenerHolder {
		private EventListener listener;
		private boolean sync;
		
		public void toggleEvent(Event e){
			if(sync)
				listener.fireEvent(e);
			else
				ThreadFactory.getFactory().createThread(()->{
					listener.fireEvent(e);
				}).start();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass()){
				if(obj instanceof EventListener)
				return obj.equals(listener);
			}
			EventListenerHolder other = (EventListenerHolder) obj;
			if (listener == null) {
				if (other.listener != null)
					return false;
			} else if (!listener.equals(other.listener))
				return false;
			if (sync != other.sync)
				return false;
			return true;
		}
	}
	
	private Client handle;
	
	private HashMap<EventType, SpecificEventManager> managers = new HashMap<>();
	private ArrayList<EventListenerHolder> listeners  = new ArrayList<>();
	
	@Getter
	@Setter
	private boolean sync = false;
	
	public EventManager(Client client) {
		this.handle = client;
		for(EventType type : EventType.values())
			managers.put(type, new SpecificEventManager(type,this));
	}
	
	public void registerListener(EventListener listener){
		registerListener(listener, true);
	}
	public void registerListener(EventListener listener,boolean sync){
		listeners.add(new EventListenerHolder(listener, sync));
	}
	public void deregisterListener(EventListener listener){
		listeners.remove(listener);
	}
	
	public List<EventListener> getListener(){
		ArrayList<EventListener> out = new ArrayList<>();
		for(EventListenerHolder h : listeners)
			out.add(h.getListener());
		return Collections.unmodifiableList(out);
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
		writePacket(packet,sync);
	}
	
	protected void writePacket(Packet packet,boolean sync){
		if(sync)
			handle.writePacket(packet).getSync();
		else
			handle.writePacket(packet).getAsync(new Callback<PacketOutPacketStatus.Error[]>() {
				@Override
				public void call(PacketOutPacketStatus.Error[] obj,Throwable e) {
					if(e != null)
						e.printStackTrace();
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

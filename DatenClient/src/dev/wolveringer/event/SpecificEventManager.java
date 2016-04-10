package dev.wolveringer.event;

import java.util.ArrayList;
import java.util.HashMap;

import dev.wolveringer.dataserver.protocoll.packets.PacketEventCondition;
import dev.wolveringer.dataserver.protocoll.packets.PacketEventTypeSettings;
import dev.wolveringer.events.ChangeableEventCondition;
import dev.wolveringer.events.ConditionChangeListener;
import dev.wolveringer.events.EventCondition;
import dev.wolveringer.events.EventConditions;
import dev.wolveringer.events.EventType;
import lombok.Getter;

public class SpecificEventManager {
	@Getter
	private static class ToggleableEventCondition<T> implements ConditionChangeListener<T>{
		private EventCondition<T> condition;
		private boolean active;
		private SpecificEventManager handle;
		
		public ToggleableEventCondition(EventConditions condition,SpecificEventManager handle) {
			this.condition = condition.createCondition();
			this.handle = handle;
		}
		
		public void setActive(boolean flag){
			System.out.println("Setactive: "+flag+":"+active);
			if(active == flag)
				return;
			active = flag;
			if(active){
				((ChangeableEventCondition)this.condition).addListener(this);
				System.out.println("Active listener");
			}else{
				((ChangeableEventCondition)this.condition).removeListener(this);
				System.out.println("deactive listener");
			}
		}

		@Override
		public void onValueAdd(EventCondition con, T obj) {
			System.out.println("Update add: "+active+":"+con.getCondition());
			handle.updateEventCondition(this);
		}

		@Override
		public void onValueRemove(EventCondition con, T obj) {
			System.out.println("Update remove: "+active+":"+con.getCondition());
			handle.updateEventCondition(this);
		}
	}
	
	private boolean enabled;
	
	private HashMap<EventConditions, ToggleableEventCondition> conditions = new HashMap<>();
	@Getter
	private EventType type;
	private EventManager handle;
	
	public SpecificEventManager(EventType type,EventManager handle) {
		this.type = type;
		this.handle = handle;
		for(EventConditions c : type.getAvariableConditions())
			conditions.put(c, new ToggleableEventCondition<>(c, this));
	}
	
	public void setEventEnabled(boolean enabled) {
		this.enabled = enabled; //TODO sync with datenserver!
		this.handle.writePacket(new PacketEventTypeSettings(type, enabled, getAllActiveConditions()));
	}
	
	public void setEventEnabled(boolean enabled,boolean sync) {
		this.enabled = enabled; //TODO sync with datenserver!
		this.handle.writePacket(new PacketEventTypeSettings(type, enabled, getAllActiveConditions()),sync);
	}
	
	public boolean isEventEnabled() {
		return enabled;
	}
	
	public boolean isConditionEnabled(EventConditions type){
		if(!conditions.containsKey(type))
			return false;
		return conditions.get(type).isActive();
	}
	public void setConditionEnables(EventConditions type,boolean flag){
		if(!conditions.containsKey(type))
			throw new RuntimeException("Eventtype "+getType()+" dont support condition "+type);
		conditions.get(type).setActive(flag);
	}
	public EventCondition getCondition(EventConditions type){
		return getCondition(type, type.getConditionType());
	}
	
	public <T> EventCondition<T> getCondition(EventConditions type,Class<T> conditionType){
		if(!conditions.containsKey(type))
			throw new RuntimeException("Eventtype "+getType()+" dont support condition "+type);
		return conditions.get(type).getCondition();
	}
	
	public ArrayList<EventCondition<?>> getAllActiveConditions(){
		ArrayList<EventCondition<?>> cons = new ArrayList<>();
		for(ToggleableEventCondition<?> t : conditions.values())
			if(t.isActive())
				cons.add(t.getCondition());
		return cons;
	}
	
	protected void updateEventCondition(ToggleableEventCondition condition){
		System.out.println("Condition updated: "+condition.getCondition().getCondition()+" Active: "+condition.isActive());
		handle.writePacket(new PacketEventCondition(type, condition.getCondition().getCondition(), condition.active,condition.getCondition()));
	}

	public void updateAll() {
		setEventEnabled(false,true);
		setEventEnabled(true,true);
	}
}

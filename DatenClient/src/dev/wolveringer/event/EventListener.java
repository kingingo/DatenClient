package dev.wolveringer.event;

import dev.wolveringer.events.Event;

public interface EventListener {
	public void fireEvent(Event e);
}

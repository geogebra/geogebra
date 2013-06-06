package geogebra.common.move.events;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 * Host for the Common event handling
 */
public abstract class BaseEventPool {
	
	/**
	 * list of events
	 */
	protected ArrayList<BaseEvent> eventList = null;
	
	/**
	 * Instantiates the Event handling Code
	 */
	public BaseEventPool() {
		
	}
	
	/**
	 * run over the events, and triggers them.
	 */
	public void trigger() {
		Iterator<BaseEvent> events = eventList.iterator();
		while (events.hasNext()) {
			events.next().trigger();
		}
	}
	
	
	
	
	
	
	

}

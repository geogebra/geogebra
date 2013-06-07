package geogebra.common.move.events;

import geogebra.common.move.operations.BaseOperation;

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
	 * operation of the given event
	 */
	protected BaseOperation operation = null;
	
	/**
	 * Instantiates the Event handling Code
	 * @param operation Operation for the given eventPool
	 */
	public BaseEventPool(BaseOperation op) {
		operation = op;
	}
	
	/**
	 * run over the events, and triggers them.
	 */
	public void trigger() {
		if (eventList != null) {
			Iterator<BaseEvent> events = eventList.iterator();
			while (events.hasNext()) {
				events.next().trigger();
			}
		}
	}
	
	
	
	
	
	
	

}

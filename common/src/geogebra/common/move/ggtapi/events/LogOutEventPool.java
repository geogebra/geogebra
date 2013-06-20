package geogebra.common.move.ggtapi.events;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.events.BaseEventPool;
import geogebra.common.move.operations.BaseOperation;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 *	Event pool for Log Out events
 */
public class LogOutEventPool extends BaseEventPool {

	/**
	 * @param op
	 * Creates a new eventPool for logOut events
	 */
	public LogOutEventPool(BaseOperation op) {
		super(op);
	}
	
	/**
	 * @param event LogOutEvent
	 * Registers a new LogOut event
	 */
	public void onLogOut(LogOutEvent event) {
		if (eventList == null) {
			eventList = new ArrayList<BaseEvent>();
		}
		eventList.add(event);
	}
	
	/**
	 * @param name String
	 * removes an event with a given name,
	 * or the whole event array, if String is null
	 */
	public void offLogOut(String name) {
		if (eventList != null) {
			if (name == null) {
				eventList = null;
			} else {
				Iterator<BaseEvent> it = eventList.iterator();
				while (it.hasNext()) {
					if (it.next().getName() == name) {
						eventList.remove(it.next());
					}
				}
			}
		}
	}

}

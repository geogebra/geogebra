package geogebra.common.move.events;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 * List for online events
 */
public class OnlineEventPool extends BaseEventPool {

	/**
	 * @param event OnlineEvent
	 * Registers a new online event
	 */
	public void onOnline(OnLineEvent event) {
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
	public void offOnline(String name) {
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

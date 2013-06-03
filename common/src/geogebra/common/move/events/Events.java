package geogebra.common.move.events;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 * Host for the Common event handling
 */
public class Events {
	
	private ArrayList<OnLineEvent> onlineEvents = null;
	private ArrayList<OffLineEvent> offlineEvents = null;
	
	/**
	 * Instantiates the Event handling Code
	 */
	public Events() {
		
	}
	
	/**
	 * @param event OnlineEvent
	 * Registers a new online event
	 */
	public void onOnline(OnLineEvent event) {
		if (onlineEvents == null) {
			onlineEvents = new ArrayList<OnLineEvent>();
		}
		onlineEvents.add(event);
		
	}
	
	/**
	 * @param name String
	 * removes an event with a given name,
	 * or the whole event array, if String is null
	 */
	public void offOnline(String name) {
		if (onlineEvents != null) {
			if (name == null) {
				onlineEvents = null;
			} else {
				Iterator<OnLineEvent> it = onlineEvents.iterator();
				while (it.hasNext()) {
					if (it.next().getName() == name) {
						onlineEvents.remove(it.next());
					}
				}
			}
		}
	}
	
	
	/**
	 * @param event OnlineEvent
	 * Registers a new online event
	 */
	public void onOffline(OffLineEvent event) {
		if (offlineEvents == null) {
			offlineEvents = new ArrayList<OffLineEvent>();
		}
		offlineEvents.add(event);
	}
	
	/**
	 * @param name String
	 * removes an event with a given name,
	 * or the whole event array, if String is null
	 */
	public void offOffline(String name) {
		if (offlineEvents != null) {
			if (name == null) {
				offlineEvents = null;
			} else {
				Iterator<OffLineEvent> it = offlineEvents.iterator();
				while (it.hasNext()) {
					if (it.next().getName() == name) {
						offlineEvents.remove(it.next());
					}
				}
			}
		}
	}
	
	

}

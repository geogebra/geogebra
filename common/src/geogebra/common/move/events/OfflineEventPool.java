package geogebra.common.move.events;

import geogebra.common.move.operations.NetworkOperation;
import geogebra.common.move.views.OfflineView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author gabor
 * 
 * Pool for Offline events
 *
 */
public class OfflineEventPool extends BaseEventPool {

	/**
	 * @param op OfflineOperation
	 */
	public OfflineEventPool(NetworkOperation op) {
		super(op);
	}

	/**
	 * @param event OnlineEvent
	 * Registers a new online event
	 */
	public void onOffline(OffLineEvent event) {
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
	public void offOffline(String name) {
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
	
	@Override
	public void trigger() {
		((OfflineView) operation.getView()).render(false);
		((NetworkOperation) operation).setOnline(false);
	}
}

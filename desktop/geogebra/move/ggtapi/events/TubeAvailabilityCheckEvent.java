package geogebra.move.ggtapi.events;

import geogebra.common.move.events.BaseEvent;

/**
 * @author gabor
 * 	Event for login operations
 *
 */
public class TubeAvailabilityCheckEvent extends BaseEvent {
	private boolean available;
	
	/**
	 * @param available Whether Tube is available
	 */
	public TubeAvailabilityCheckEvent(boolean available) {
		this.available = available;
	}

	/**
	 * @return Whether Tube is available
	 */
	public boolean isAvailable() {
		return available;
	}

	@Override
	public void trigger() {
		// No action
	}
}

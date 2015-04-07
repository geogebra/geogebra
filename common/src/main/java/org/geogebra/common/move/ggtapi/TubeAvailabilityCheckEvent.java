package org.geogebra.common.move.ggtapi;

import org.geogebra.common.move.events.BaseEvent;

/**
 * @author gabor Event for login operations
 * 
 */
public class TubeAvailabilityCheckEvent extends BaseEvent {
	private boolean available;

	/**
	 * @param available
	 *            Whether Tube is available
	 */
	public TubeAvailabilityCheckEvent(boolean available) {
		super("tube check");
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

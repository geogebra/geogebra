package org.geogebra.common.move.events;

/**
 * used if the user decides not to log in
 */
public class StayLoggedOutEvent extends BaseEvent {

	/**
	 * @param name
	 *            name associated with the event (can be null)
	 */
	public StayLoggedOutEvent(String name) {
		super(name);
	}

	@Override
	public void trigger() {
		// no action
	}

}

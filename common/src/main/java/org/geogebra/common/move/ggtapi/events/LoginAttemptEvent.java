package org.geogebra.common.move.ggtapi.events;

import org.geogebra.common.move.events.BaseEvent;

/**
 * This event is triggered when an attempt to login the user is started
 *
 * @author stefan
 */
public class LoginAttemptEvent extends BaseEvent {

	/**
	 * Creates a new Login attempt event.
	 */
	public LoginAttemptEvent() {
		super("Attempt");
	}

}

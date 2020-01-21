package org.geogebra.common.move.ggtapi.events;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;

/**
 * @author stefan
 * 
 *         This event is triggered when an attempt to login the user is started
 *
 */
public class LoginAttemptEvent extends BaseEvent {

	/**
	 * Creates a new Login attempt event,
	 */
	public LoginAttemptEvent() {
		super("Attempt");
	}

}

package org.geogebra.common.move.ggtapi.events;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;

/**
 * @author stefan
 * 
 * This event is triggered when an attempt to login the user is started
 *
 */
public class LoginAttemptEvent extends BaseEvent {
	private GeoGebraTubeUser user;
	
	/**
	 * Creates a new Login attempt event,
	 * @param user The user that is going to be logged in. At this state only the login token may be defined.
	 * 
	 */
	public LoginAttemptEvent(GeoGebraTubeUser user) {
		super("Attempt");
		this.user = user;
	}

	/**
	 * @return The user that is going to be logged in. At this state onyl the login token may be defined.
	 */
	public GeoGebraTubeUser getUser() {
		return user;
	}

	@Override
	public void trigger() {
		// No action
	}
}

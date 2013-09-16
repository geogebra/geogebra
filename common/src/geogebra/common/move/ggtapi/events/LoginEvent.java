package geogebra.common.move.ggtapi.events;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;

/**
 * @author gabor
 * 	Event for login operations
 *
 */
public class LoginEvent extends BaseEvent {
	private GeoGebraTubeUser user;
	private boolean successful;
	
	/**
	 * Creates a new Login event,
	 * @param user The user that was logged in
	 * 
	 */
	public LoginEvent(GeoGebraTubeUser user, boolean successful) {
		this.user = user;
		this.successful = successful;
	}

	/**
	 * @return if the login attempt was successful
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @return the logged in user including all user information
	 */
	public GeoGebraTubeUser getUser() {
		return user;
	}

	@Override
	public void trigger() {
		// No action
	}
}

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
	private boolean automatic;
	
	/**
	 * Creates a new Login event,
	 * @param user The user that was logged in
	 * @param successful If the Login was successful 
	 * @param automatic true if the login was performed automatically (on startup) or manually by the user
	 */
	public LoginEvent(GeoGebraTubeUser user, boolean successful, boolean automatic) {
		this.user = user;
		this.successful = successful;
		this.automatic = automatic;
	}

	/**
	 * @return if the login attempt was successful
	 */
	public boolean isSuccessful() {
		return successful;
	}
	
	/**
	 * @return if the login attempt was successful
	 */
	public boolean isAutomatic() {
		return automatic;
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

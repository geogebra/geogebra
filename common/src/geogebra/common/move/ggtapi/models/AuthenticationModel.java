package geogebra.common.move.ggtapi.models;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.models.BaseModel;

/**
 * @author gabor
 * Base class for login logout operations
 *
 */
public abstract class AuthenticationModel extends BaseModel {
	private GeoGebraTubeUser loggedInUser = null;

	/**
	 * token name for user logged in got back from GGT
	 */
	public static String GGB_TOKEN_KEY_NAME = "token";

	/**
	 * Class constructor for login and logout operations
	 */
	public AuthenticationModel() {
		super();
	}

	@Override
	public void onEvent(BaseEvent event) {
		if (event instanceof LoginEvent) {
			LoginEvent loginEvent = (LoginEvent) event;
			if (loginEvent.isSuccessful()) {
				onLoginSuccess(loginEvent.getUser());
			} else {
				onLoginError(loginEvent.getUser());
			}
		} else if (event instanceof LogOutEvent) {
			clearLoginToken();
			loggedInUser = null;
		}
	}

	/**
	 * @param token The token to store
	 * Stores the token in localStorage or with any other client side method.
	 */
	public abstract void storeLoginToken(String token);
	
	/**
	 * @return The stored Token or null if not token stored
	 */
	public abstract String getLoginToken();

	/**
	 * Clears the login token from localStorage, or from other storage place used
	 */
	public abstract void clearLoginToken();
	
	/**
	 * @param response from GGT
	 * Parses the response, and sets model dependent things (localStorage, etc).
	 */
	public void onLoginSuccess(GeoGebraTubeUser user) {
		
		// Remember the logged in user
		this.loggedInUser = user;
		
		// Store the token in the storage
		if (user.getLoginToken() != this.getLoginToken()) {
			storeLoginToken(user.getLoginToken());
		}
	}

	/**
	 * @param response from GGT
	 * error happened, cleanup, etc
	 */
	public void onLoginError(GeoGebraTubeUser user) {
		if (getLoginToken() != null) {
			clearLoginToken();
		}
	}

	/**
	 * @return the Username of the currently logged in user or null if no user is logged in
	 */
	public String getUserName() {
		if (loggedInUser != null) {
			return loggedInUser.getUserName();
		}
		return null;
	}
	
	/**
	 * @return the Username of the currently logged in user or null if no user is logged in
	 */
	public int getUserId() {
		if (loggedInUser != null) {
			return loggedInUser.getUserId();
		}
		return -1;
	}
	
	/**
	 * @return The currently logged in user or null if no user is logged in
	 */
	public GeoGebraTubeUser getLoggedInUser() {
		return loggedInUser;
	}
	
	/**
	 * @return true, if a user is currently logged in or false otherwise.
	 */
	public boolean isLoggedIn() {
		if (loggedInUser == null) {
			return false;
		}
		return true;
	}
}
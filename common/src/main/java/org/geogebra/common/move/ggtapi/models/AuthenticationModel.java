package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.models.BaseModel;

/**
 * @author gabor
 * Base class for login logout operations
 *
 */
public abstract class AuthenticationModel extends BaseModel {
	private GeoGebraTubeUser loggedInUser = null;

	private boolean stayLoggedOut;

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
				onLoginSuccess(loginEvent.getUser(), loginEvent.getJSON());
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
	public void onLoginSuccess(GeoGebraTubeUser user, String json) {
		this.stayLoggedOut = false;
		// Remember the logged in user
		this.loggedInUser = user;
		storeLastUser(json);
		// Store the token in the storage
		if (user.getLoginToken() != this.getLoginToken()) {
			storeLoginToken(user.getLoginToken());
		}
	}

	protected abstract void storeLastUser(String s);

	/**
	 * @param response from GGT
	 * error happened, cleanup, etc
	 */
	public void onLoginError(GeoGebraTubeUser user) {
		this.stayLoggedOut = false;
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

	public void startOffline(GeoGebraTubeAPI api) {
		if(this.loadLastUser()!= null){
			GeoGebraTubeUser offline = new GeoGebraTubeUser(null);
			if(api.parseUserDataFromResponse(offline, this.loadLastUser())){
				this.loggedInUser = offline;
			}
		}
		
	}

	public abstract String loadLastUser();

	public void stayLoggedOut() {
		this.stayLoggedOut = true;
	}
	
	public boolean mayLogIn(){
		return !stayLoggedOut;
	}
	
}
package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.models.BaseModel;

/**
 * @author gabor Base class for login logout operations
 *
 */
public abstract class AuthenticationModel extends BaseModel {
	private GeoGebraTubeUser loggedInUser = null;

	private boolean stayLoggedOut;

	/**
	 * token name for user logged in got back from GGT
	 */
	public static String GGB_TOKEN_KEY_NAME = "token";

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
	 * @param token
	 *            The token to store Stores the token in localStorage or with
	 *            any other client side method.
	 */
	public abstract void storeLoginToken(String token);

	/**
	 * @return The stored Token or null if not token stored
	 */
	public abstract String getLoginToken();

	/**
	 * Clears the login token from localStorage, or from other storage place
	 * used
	 */
	public abstract void clearLoginToken();

	/**
	 * @param user
	 *            ggb tube user
	 * @param json
	 *            from GGT Parses the response, and sets model dependent things
	 *            (localStorage, etc).
	 */
	public void onLoginSuccess(GeoGebraTubeUser user, String json) {
		this.stayLoggedOut = false;
		// Remember the logged in user
		this.loggedInUser = user;
		storeLastUser(json);
		// Store the token in the storage
		if (!user.getLoginToken().equals(this.getLoginToken())) {
			storeLoginToken(user.getLoginToken());
		}
	}

	protected abstract void storeLastUser(String s);

	/**
	 * @param user ggb tube user
	 *            from GGT error happened, cleanup, etc
	 */
	public void onLoginError(GeoGebraTubeUser user) {
		this.stayLoggedOut = false;
		if (getLoginToken() != null || user.isShibbolethAuth()) {
			clearLoginTokenForLogginError();
		}
	}

	/**
	 * override this method if another behaviour needed
	 */
	public void clearLoginTokenForLogginError() {
		clearLoginToken();
	}

	/**
	 * @return the Username of the currently logged in user or null if no user
	 *         is logged in
	 */
	public String getUserName() {
		if (loggedInUser != null) {
			return loggedInUser.getUserName();
		}
		return null;
	}

	/**
	 * @return list of group IDs for current user
	 */
	public ArrayList<String> getUserGroups() {
		if (loggedInUser != null) {
			return loggedInUser.getGroups();
		}
		return null;
	}

	/**
	 * @return the Username of the currently logged in user or null if no user
	 *         is logged in
	 */
	public int getUserId() {
		if (loggedInUser != null) {
			return loggedInUser.getUserId();
		}
		return -1;
	}

	/**
	 * @return the language of the currently logged in user or empty string if no
	 *         user is logged in
	 */
	public String getUserLanguage() {
		if (loggedInUser != null) {
			return loggedInUser.getLanguage();
		}
		return "";
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

	/**
	 * Initialize for offline use (assume last user logged in).
	 * 
	 * @param api
	 *            tube API
	 */
	public void startOffline(BackendAPI api) {
		if (this.loadLastUser() != null) {
			GeoGebraTubeUser offline = new GeoGebraTubeUser(null);
			if (api.parseUserDataFromResponse(offline, this.loadLastUser())) {
				this.loggedInUser = offline;
			}
		}
	}

	/**
	 * @return last user from local storage
	 */
	public abstract String loadLastUser();

	/**
	 * User closed login explicitly, save a flag not to ask again.
	 */
	public void stayLoggedOut() {
		this.stayLoggedOut = true;
	}

	/**
	 * @return false if user closed login explicitly
	 */
	public boolean mayLogIn() {
		return !stayLoggedOut;
	}

}
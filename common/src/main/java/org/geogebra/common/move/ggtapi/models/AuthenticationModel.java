package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.util.GTimer;

/**
 * Base class for login logout operations
 * @author gabor
 */
public abstract class AuthenticationModel {
	private GeoGebraTubeUser loggedInUser = null;
	// session time 115 min
	public static final int SESSION_TIME = 6900000;
	// log out timer 5 min
	public static final int LOG_OUT_TIME = 300000;
	private GTimer sessionExpireTimer;
	private GTimer logOutTimer;

	/**
	 * Storage key for the login token (access token in OAuth)
	 */
	protected static final String GGB_TOKEN_KEY_NAME = "token";

	public static final String CSRF_TOKEN_KEY_NAME = "X-CSRF-TOKEN";

	private boolean preventLoginPrompt;
	private boolean loginStarted;

	private String csrfToken = "";

	/**
	 * @param loginEvent login event
	 */
	public void onLogin(LoginEvent loginEvent) {
		this.loginStarted = false;
		if (loginEvent.isSuccessful()) {
			onLoginSuccess(loginEvent.getUser(), loginEvent.getJSON());
		} else {
			onLoginError(loginEvent.getUser());
		}
	}

	/**
	 * Update after logout
	 */
	public void onLogout() {
		clearLoginToken();
		loggedInUser = null;
	}

	/**
	 * Keep track of started passive login.
	 */
	public void setLoginStarted() {
		this.loginStarted = true;
	}

	/**
	 * Stores the token in localStorage or with any other client side method.
	 * @param token The token to store.
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
	 * @param cookieName name of the cookie we want
	 * @return the value of the cookie
	 */
	public String getCookie(String cookieName) {
		return null;
	}

	/**
	 * Stores the token in localStorage or with any other client side method.
	 * @param csrfToken The token to store.
	 */
	public void storeCSRFToken(String csrfToken) {
		this.csrfToken = csrfToken;
	}

	/**
	 * @return The stored CSRF Token or null if no token stored
	 */
	public String getCSRFToken() {
		return csrfToken;
	}

	/**
	 * @param user ggb tube user
	 * @param json from GGT Parses the response, and sets model dependent things
	 * (localStorage, etc).
	 */
	private void onLoginSuccess(GeoGebraTubeUser user, String json) {
		this.preventLoginPrompt = false;
		// Remember the logged in user
		this.loggedInUser = user;
		storeLastUser(json);
		// Store the token in the storage
		if (!user.getLoginToken().equals(this.getLoginToken())) {
			storeLoginToken(user.getLoginToken());
		}
		startSessionTimer();
	}

	private void startSessionTimer() {
		if (sessionExpireTimer != null) {
			sessionExpireTimer.start();
		}
	}

	protected abstract void storeLastUser(String s);

	/**
	 * @param user ggb tube user
	 * from GGT error happened, cleanup, etc
	 */
	private void onLoginError(GeoGebraTubeUser user) {
		this.preventLoginPrompt = false;
		if (getLoginToken() != null || user.isShibbolethAuth()) {
			clearLoginToken();
		}
	}

	/**
	 * @return the Username of the currently logged in user or null if no user
	 * is logged in
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
	public ArrayList<GroupIdentifier> getUserGroups() {
		if (loggedInUser != null) {
			return loggedInUser.getGroups();
		}
		return null;
	}

	/**
	 * @return the Username of the currently logged in user or null if no user
	 * is logged in
	 */
	public int getUserId() {
		if (loggedInUser != null) {
			return loggedInUser.getUserId();
		}
		return -1;
	}

	/**
	 * @return the language of the currently logged in user or empty string if no
	 * user is logged in
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
		return loggedInUser != null;
	}

	/**
	 * Initialize for offline use (assume last user logged in).
	 * @param api tube API
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
		this.loginStarted = false;
	}

	public void preventLoginPrompt() {
		this.preventLoginPrompt = true;
	}

	/**
	 * @return false if user closed login explicitly
	 */
	public boolean mayLogIn() {
		return !preventLoginPrompt;
	}

	/**
	 * @return whether login was initiated but not finished
	 */
	public boolean isLoginStarted() {
		return loginStarted;
	}

	public String getEncoded() {
		return null;
	}

	/**
	 * initialize timer
	 * @param timer new session timer
	 */
	public void setSessionExpireTimer(GTimer timer) {
		sessionExpireTimer = timer;
	}

	/**
	 * initialize timer
	 * @param timer new logout timer
	 */
	public void setLogOutTimer(GTimer timer) {
		logOutTimer = timer;
	}

	/**
	 * if back-end touched: restart session timer and stop logout timer
	 */
	public void restartSession() {
		resetTimer(logOutTimer);
		if (sessionExpireTimer != null && isLoggedIn()) {
			resetTimer(sessionExpireTimer);
			sessionExpireTimer.start();
		}
	}

	/**
	 * reset a timer
	 * @param timer timer
	 */
	private void resetTimer(GTimer timer) {
		if (timer != null) {
			timer.stop();
		}
	}

	/**
	 * reset both session and logout timers
	 */
	public void discardTimers() {
		resetTimer(sessionExpireTimer);
		resetTimer(logOutTimer);
	}
}

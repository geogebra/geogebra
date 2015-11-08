package org.geogebra.common.move.ggtapi.operations;

import org.geogebra.common.main.App;
import org.geogebra.common.move.events.StayLoggedOutEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginAttemptEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.operations.BaseOperation;
import org.geogebra.common.move.views.EventRenderable;

/**
 * @author stefan
 * 
 * Operational class for login functionality
 *
 */
public abstract class LogInOperation extends BaseOperation<EventRenderable> {

	@Override
	public AuthenticationModel getModel() {
		return (AuthenticationModel) super.getModel();
	}
	
	/**
	 * @return the user name from the storage
	 */
	public String getUserName() {
		return getModel().getUserName();
	}

	/**
	 * @return boolean indicating that the user is already logged in.
	 */
	public final boolean isLoggedIn() {
		return getModel().isLoggedIn();
	}
	
	/**
	 * Reads the stored login token from the storage and sends a request to the API to authorize the token.
	 * On successful login, the user information will be stored in the user object of the authorization model
	 */
	public final void performTokenLogin() {
		String token = getModel().getLoginToken();
		if (token != null) {
			performTokenLogin(token, true);
		}else{
			getGeoGebraTubeAPI().checkAvailable(this);
		}
	}
	

	/**
	 * Sends a request to the API to authorize the specified token
	 * On successful login, the user information will be stored in the user object of the authorization model
	 * The API call is executed in an own thread to keep the GUI responsive. 
	 * The start of the login attempt will be indicated by sending an {@link LoginAttemptEvent} on the GUI thread.
	 * When the login attempt is finished (successful or not), a {@link LoginEvent} will be sent
	 * 
	 * @param token The Login token to authorize
	 * @param automatic If the login is triggered automatically or by the user. This information will be provided
	 * 					in the Login Event. 
	 */
	public void performTokenLogin(String token, boolean automatic) {
		if("".equals(token)){
			stayLoggedOut();
			return;
		}
		doPerformTokenLogin(new GeoGebraTubeUser(token), automatic);
	}
	
	public void stayLoggedOut() {
		getModel().stayLoggedOut();
		onEvent(new StayLoggedOutEvent(null));
	}

	public void performCookieLogin(String cookie) {
		doPerformTokenLogin(new GeoGebraTubeUser(null, cookie), true);
	}
	/**
	 * Performs the API call to authorize the token.
	 * @param token the token to authorize
	 * @param automatic If the login is triggered automatically or by the user. This information will be provided
	 * 					in the Login Event. 
	 */
	protected void doPerformTokenLogin(final GeoGebraTubeUser user , final boolean automatic) {
		GeoGebraTubeAPI api = getGeoGebraTubeAPI();
		

		App.debug("Sending call to GeoGebraTube API to authorize the login token...");

		// Trigger an event to signal the login attempt
		onEvent(new LoginAttemptEvent(user));
		

		// Send API request to check if the token is valid
		api.authorizeUser(user, this, automatic);
		
	}
	
	/**
	 * Handle the logout
	 */
	public void performLogOut() {
		onEvent(new LogOutEvent());
	}
	
	/**
	 * @return An instance of the GeoGebraTubeAPI
	 */
	public abstract GeoGebraTubeAPI getGeoGebraTubeAPI();
	

	/**
	 * @param languageCode The code of the current user language. This code will be used as URL parameter
	 * @return The URL to the GeoGebraTube Login page including params for the client identification
	 * and the expiration time.
	 */
	public String getLoginURL(String languageCode) {
		return "https://accounts.geogebra.org/user/signin"
				+ "/caller/"+getURLLoginCaller()
				+"/expiration/"+getURLTokenExpirationMinutes()
				+"/clientinfo/"+getURLClientInfo()
				+"/?lang="+languageCode;
	}
	
	/**
	 * @return The name of the caller of the login page in GeoGebraTube.
	 * This is used to show different layouts of the login page. Currently supported callers are: desktop, web, touch
	 */
	protected abstract String getURLLoginCaller();
	
	/**
	 * @return the Expiration time of the login token in minutes.
	 * the default implementation returns 129600 = 90 days. 
	 */
	protected String getURLTokenExpirationMinutes() {
		return "129600"; // = 90 days
	}
	
	/**
	 * @return The client information string for the login token. This String is stored in the GeoGebraTube database.
	 * The returned string must be a valid URL encoded String. (use URLEncoder.encode). 
	 */
	protected abstract String getURLClientInfo();

	public void startOffline() {
		getModel().startOffline(getGeoGebraTubeAPI());
		
	}

	public boolean mayLogIn() {
		return getModel().mayLogIn();
	}

	public boolean owns(Material mat) {
		return mat.getAuthorID() <= 0
				|| mat.getAuthorID() == getModel().getUserId();
	}
}

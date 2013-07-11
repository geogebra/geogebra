package geogebra.common.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;
import geogebra.common.move.models.BaseModel;

/**
 * @author gabor
 * Base class for login logout operations
 *
 */
public abstract class AuthenticationModel extends BaseModel {

	/**
	 * token name for user logged in got back from GGT
	 */
	public static String GGB_TOKEN_KEY_NAME = "token";
	/**
	 * used for store any other thing got back concerning login
	 */
	public static String GGB_LOGIN_DATA_KEY_NAME ="login";
	
	/**
	 * used to store the username in localstorage
	 */
	public static String GGB_LOGIN_DATA_USERNAME_KEY_NAME = "username";

	/**
	 * Class constructor for login and logout operations
	 */
	public AuthenticationModel() {
		super();
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
	 * @return gets the login data from localStorage or from other storage places
	 */
	public abstract JSONObject getStoredLoginData();
	
	/**
	 * @param info the login info to store
	 */
	public abstract void storeLoginData(JSONObject info);
	
	/**
	 * removes the stored login data
	 */
	public abstract void removeStoredLoginData();
	
	/**
	 * @param response from GGT
	 * Parses the response, and sets model depenent things (localStorage, etc).
	 */
	public void loginSuccess(JSONObject response) {
		JSONString token = (JSONString) response.get(GGB_TOKEN_KEY_NAME);
		if (token != null) {
			storeLoginToken(token.toString());
		}
		
		JSONObject loginInfo = (JSONObject) response.get(GGB_LOGIN_DATA_KEY_NAME);
		if (loginInfo != null) {
			storeLoginData(loginInfo);		
		}
	}

	/**
	 * @param response from GGT
	 * error happened, cleanup, etc
	 */
	public void loginError(JSONObject response) {
		if (getLoginToken() != null) {
			clearLoginToken();
		}
	}


}
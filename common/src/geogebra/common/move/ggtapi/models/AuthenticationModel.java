package geogebra.common.move.ggtapi.models;

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
	protected static String GGB_TOKEN_KEY_NAME = "token";

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

}
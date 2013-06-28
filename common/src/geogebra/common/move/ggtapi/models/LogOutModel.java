package geogebra.common.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.json.JSONObject;

/**
 * @author gabor
 * Model for logout operations
 */
public abstract class LogOutModel extends AuthenticationModel {
	
	/**
	 * Creates a new LogOutModel
	 */
	public LogOutModel() {
		
	}

	/**
	 * @param response
	 * clears the token on logOut
	 */
	public void logOutSuccess(JSONObject response) {
		clearLoginToken();
	}

	/**
	 * @param response got back from GGT
	 * clears the token on logoutError operation
	 */
	public void logOutError(JSONObject response) {
		clearLoginToken();
	}
}

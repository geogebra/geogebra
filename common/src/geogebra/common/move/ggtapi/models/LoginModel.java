package geogebra.common.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;

/**
 * @author gabor
 * Model for loginOperation
 *
 */
public abstract class LoginModel extends AuthenticationModel {
	
	/**
	 * Creates a new login model
	 */
	public LoginModel() {
		
	}

	/**
	 * @param response from GGT
	 * Parses the response, and sets model depenent things (localStorage, etc).
	 */
	public void loginSuccess(JSONObject response) {
		JSONString token = (JSONString) response.get(GGB_TOKEN_KEY_NAME);
		if (token.isString() != null) {
			storeLoginToken(token.toString());
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

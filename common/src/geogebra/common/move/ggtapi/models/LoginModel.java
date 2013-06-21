package geogebra.common.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.models.BaseModel;

/**
 * @author gabor
 * Model for loginOperation
 *
 */
public class LoginModel extends BaseModel {
	
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
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param response from GGT
	 * error happened, cleanup, etc
	 */
	public void loginError(JSONObject response) {
		// TODO Auto-generated method stub
		
	}

}

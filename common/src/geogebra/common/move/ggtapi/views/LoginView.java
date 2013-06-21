package geogebra.common.move.ggtapi.views;

import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.views.BaseView;

/**
 * @author gabor
 *  View class for Login functionality
 */
public class LoginView extends BaseView {
	
	/**
	 * Creates a new Login view
	 */
	public LoginView() {
		super();
	}

	/**
	 * @param response from GGT
	 * sets the successfull response 
	 */
	public void loginSuccess(JSONObject response) {
		
	}

	/**
	 * @param response from GGT
	 * Error happened, must be shown if view
	 */
	public void loginError(JSONObject response) {
		// TODO Auto-generated method stub
		
	}

}

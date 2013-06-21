package geogebra.common.move.ggtapi.operations;

import geogebra.common.move.ggtapi.models.LoginModel;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.views.LoginView;
import geogebra.common.move.operations.BaseOperation;

/**
 * @author gabor
 * 
 * Operational class for login functionality
 *
 */
public class LoginOperation extends BaseOperation {
	
	/**
	 * Creates a new Login operation
	 */
	public LoginOperation()  {
		
	}

	/**
	 * @param response from GGT
	 * 
	 * Successfull login operation
	 * 
	 */
	public void loginSuccess(JSONObject response) {
		((LoginModel) getModel()).loginSuccess(response);
		((LoginView) getView()).loginSuccess(response);
	}

	/**
	 * @param response from GGT
	 * 
	 * Error happened during login
	 */
	public void loginError(JSONObject response) {
		((LoginModel) getModel()).loginError(response);
		((LoginView) getView()).loginError(response);
	}

}

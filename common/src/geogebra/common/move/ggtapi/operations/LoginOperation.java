package geogebra.common.move.ggtapi.operations;

import geogebra.common.move.ggtapi.events.LoginEventPool;
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
		getModel().loginSuccess(response);
		getView().loginSuccess(response);
	}

	/**
	 * @param response from GGT
	 * 
	 * Error happened during login
	 */
	public void loginError(JSONObject response) {
		getModel().loginError(response);
		getView().loginError(response);
	}
	
	@Override
	public LoginModel getModel() {
		return (LoginModel) super.getModel();
	}
	
	@Override
	public LoginView getView() {
		return (LoginView) super.getView();
	}
	
	@Override
	public LoginEventPool getEvent() {
		return (LoginEventPool) super.getEvent();
	}

}

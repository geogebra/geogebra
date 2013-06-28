package geogebra.common.move.ggtapi.operations;

import geogebra.common.move.ggtapi.models.LogOutModel;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.views.LogOutView;
import geogebra.common.move.operations.BaseOperation;

/**
 * @author gabor
 * operation for Log out functionality
 */
public class LogOutOperation extends BaseOperation {
	
	/**
	 * Creates new LogOutOperation
	 */
	public LogOutOperation() {
		
	}
	
	/**
	 * @param response from GGT
	 * 
	 * Successfull login operation
	 * 
	 */
	public void logOutSuccess(JSONObject response) {
		getModel().logOutSuccess(response);
		getView().logOutSuccess(response);
	}

	/**
	 * @param response from GGT
	 * 
	 * Error happened during login
	 */
	public void logOutError(JSONObject response) {
		getModel().logOutError(response);
		getView().logOutError(response);
	}
	
	@Override
	public LogOutView getView() {
		return (LogOutView) super.getView();
	}
	
	@Override
	public LogOutModel getModel() {
		return (LogOutModel) super.getModel();
	}

}

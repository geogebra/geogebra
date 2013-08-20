package geogebra.common.move.ggtapi.operations;

import geogebra.common.move.ggtapi.models.AuthenticationModel;
import geogebra.common.move.ggtapi.views.LogOutView;
import geogebra.common.move.operations.BaseOperation;
import geogebra.common.move.views.Renderable;

/**
 * @author gabor
 * operation for Log out functionality
 */
public class LogOutOperation extends BaseOperation<Renderable> {
	
	/**
	 * Creates new LogOutOperation
	 */
	public LogOutOperation() {
		
	}
	
	@Override
	public LogOutView getView() {
		return (LogOutView) super.getView();
	}
	
	@Override
	public AuthenticationModel getModel() {
		return (AuthenticationModel) super.getModel();
	}

	/**
	 * Handle the logout
	 */
	public void logOut() {
		getModel().clearLoginToken();
		getModel().removeStoredLoginData();
		getView().render();
	}

}

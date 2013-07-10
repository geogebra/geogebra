package geogebra.common.move.ggtapi.operations;

import geogebra.common.move.ggtapi.events.LogOutEventPool;
import geogebra.common.move.ggtapi.models.LogOutModel;
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
	
	@Override
	public LogOutView getView() {
		return (LogOutView) super.getView();
	}
	
	@Override
	public LogOutModel getModel() {
		return (LogOutModel) super.getModel();
	}
	
	@Override
	public LogOutEventPool getEvent() {
		return (LogOutEventPool) super.getEvent();
		
	}

	public void logOut() {
		getModel().clearLoginToken();
		getView().render();
	}

}

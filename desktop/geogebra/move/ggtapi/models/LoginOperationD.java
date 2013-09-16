package geogebra.move.ggtapi.models;

import geogebra.common.GeoGebraConstants;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.move.ggtapi.views.BaseSwingEventView;
import geogebra.util.URLEncoder;

import javax.swing.SwingWorker;

/**
 * The desktop version of the login operation.
 * uses an own AuthenticationModel and an own implementation of the API
 *  
 * @author stefan
 */
public class LoginOperationD extends LogInOperation {

	/**
	 * Initializes the LoginOperation for Desktop by creating the corresponding model and view classes
	 */
	public LoginOperationD() {
		super();
		
		setView(new BaseSwingEventView());
		setModel(new AuthenticationModelD());
	}

    @Override
	public void performTokenLogin(String token) {
    	
		// Perform the API call in a background process
		LoginTokenApprover approver = new LoginTokenApprover(token);
		approver.execute();
	}

	private class LoginTokenApprover extends SwingWorker<Void, BaseEvent> {
        private String token;
        
        LoginTokenApprover(String token) {
        	super();
        	this.token = token;
        }
        
		@SuppressWarnings("synthetic-access")
		@Override
        public Void doInBackground() {
    		doPerformTokenLogin(token);
    		return null;
        }
    }

	@Override
	public GeoGebraTubeAPI getGeoGebraTubeAPI() {
		return GeoGebraTubeAPID.getInstance();
	}

	@Override
	protected String getURLLoginCaller() {
		return "desktop";
	}

	@Override
	protected String getURLClientInfo() {
		URLEncoder enc = new URLEncoder();
		return enc.encode("GeoGebra Desktop Application V" + GeoGebraConstants.VERSION_STRING);
	}
}

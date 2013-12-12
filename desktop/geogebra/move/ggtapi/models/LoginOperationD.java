package geogebra.move.ggtapi.models;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.move.ggtapi.events.TubeAvailabilityCheckEvent;
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

	boolean tubeAvailable = false;


	/**
	 * Initializes the LoginOperation for Desktop by creating the corresponding model and view classes
	 */
	public LoginOperationD() {
		super();
		
		setView(new BaseSwingEventView());
		setModel(new AuthenticationModelD());
	}
	
	@Override
	public void performTokenLogin() {
		String token = getModel().getLoginToken();
		if (token != null) {
			performTokenLogin(token, true);
		} else {
			// Check if the GeoGebraTube API is available
			checkIfAPIIsAvailable();
		}
	}
	

    @Override
	public void performTokenLogin(String token, boolean automatic) {
    	
		// Perform the API call in a background process
		LoginTokenApprover approver = new LoginTokenApprover(token, automatic);
		approver.execute();
	}

	private class LoginTokenApprover extends SwingWorker<Void, BaseEvent> {
        private String token;
        private boolean automatic;
        
        LoginTokenApprover(String token, boolean automatic) {
        	super();
        	this.token = token;
        	this.automatic = automatic;
        }
        
		@SuppressWarnings("synthetic-access")
		@Override
        public Void doInBackground() {
    		doPerformTokenLogin(token, automatic);
    		tubeAvailable = getGeoGebraTubeAPI().isAvailable();
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
	
	/**
	 * @return boolean if the tube API is available
	 */
	public boolean isTubeAvailable() {
		return tubeAvailable;
	}
	
	
	/**
	 * Sends a test request to the tube API to check if it is available
	 */
    public void checkIfAPIIsAvailable() {
    	SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				App.debug("Sending test call to check if the GeoGebraTube API is available...");
				GeoGebraTubeAPI api = getGeoGebraTubeAPI();

				tubeAvailable = api.isAvailable();
				
				// Send API request to check if the token is valid
				if (tubeAvailable) {
					App.debug("The test request to GeoGebraTube was successful");
					
				} else {
					App.debug("The GoeGebraTube API is not available");
				}
				// Trigger event to signal that the API is available
				onEvent(new TubeAvailabilityCheckEvent(tubeAvailable));
				
				return null;
			}
    		
    	};
    	worker.execute();
	}
}

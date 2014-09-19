package geogebra.move.ggtapi.models;

import geogebra.common.GeoGebraConstants;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.move.ggtapi.views.BaseSwingEventView;
import geogebra.util.URLEncoder;

import javax.swing.SwingWorker;

/**
 * The desktop version of the login operation. uses an own AuthenticationModel
 * and an own implementation of the API
 * 
 * @author stefan
 */
public class LoginOperationD extends LogInOperation {

	boolean tubeAvailable = false;

	/**
	 * Initializes the LoginOperation for Desktop by creating the corresponding
	 * model and view classes
	 */
	public LoginOperationD() {
		super();

		setView(new BaseSwingEventView());
		setModel(new AuthenticationModelD());
	}

	@Override
	public void performTokenLogin(String token, boolean automatic) {

		// Perform the API call in a background process
		doPerformTokenLogin(new GeoGebraTubeUser(token), automatic);
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
			doPerformTokenLogin(new GeoGebraTubeUser(token), automatic);
			return null;
		}
	}

	@Override
	public GeoGebraTubeAPID getGeoGebraTubeAPI() {
		return GeoGebraTubeAPID.getInstance();
	}

	@Override
	protected String getURLLoginCaller() {
		return "desktop";
	}

	@Override
	protected String getURLClientInfo() {
		URLEncoder enc = new URLEncoder();
		return enc.encode("GeoGebra Desktop Application V"
				+ GeoGebraConstants.VERSION_STRING);
	}

	/**
	 * @return boolean if the tube API is available
	 */
	public boolean isTubeAvailable() {
		return getGeoGebraTubeAPI().isAvailable();
	}

	/**
	 * @return boolean if the check for the availability of tube is finished
	 */
	public boolean isTubeCheckDone() {
		return getGeoGebraTubeAPI().isCheckDone();
	}

}

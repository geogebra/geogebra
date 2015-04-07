package org.geogebra.desktop.move.ggtapi.models;

import javax.swing.SwingWorker;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.desktop.move.ggtapi.views.BaseSwingEventView;
import org.geogebra.desktop.util.URLEncoder;

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

	private GeoGebraTubeAPID api;

	@Override
	public GeoGebraTubeAPID getGeoGebraTubeAPI() {
		if (api == null) {
			ClientInfo client = new ClientInfo();
			client.setModel((AuthenticationModel) this.model);
			client.setType("desktop");
			client.setWidth(1024);
			client.setWidth(768);
			api = new GeoGebraTubeAPID(client);
		}
		return api;
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

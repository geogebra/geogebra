package org.geogebra.web.web.move.ggtapi.operations;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.views.BaseEventView;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.URLEncoder;
import org.geogebra.web.web.move.ggtapi.models.AuthenticationModelW;
import org.geogebra.web.web.move.ggtapi.models.GeoGebraTubeAPIW;

/**
 * The web version of the login operation. uses an own AuthenticationModel and
 * an own implementation of the API
 * 
 * @author stefan
 */
public class LoginOperationW extends LogInOperation {

	private AppW app;

	/**
	 * Initializes the SignInOperation for Web by creating the corresponding
	 * model and view classes
	 * 
	 * @param appWeb
	 *            application
	 */
	public LoginOperationW(AppW appWeb) {
		super();
		this.app = appWeb;
		setView(new BaseEventView());
		setModel(new AuthenticationModelW(appWeb));

		iniNativeEvents();
	}

	private native void iniNativeEvents() /*-{
		var t = this;
		$wnd
				.addEventListener(
						"message",
						function(event) {
							var data;
							//later if event.origin....
							if (event.data) {
								data = $wnd.JSON.parse(event.data);
								if (data.action === "logintoken") {
									t.@org.geogebra.web.web.move.ggtapi.operations.LoginOperationW::processToken(Ljava/lang/String;)(data.msg);
								}
							}
						}, false);
	}-*/;

	@Override
	public GeoGebraTubeAPI getGeoGebraTubeAPI() {
		return new GeoGebraTubeAPIW(app.getClientInfo(), app.isPrerelease());

	}

	@Override
	protected String getURLLoginCaller() {
		return "web";
	}

	@Override
	protected String getURLClientInfo() {
		URLEncoder enc = new URLEncoder();
		return enc.encode("GeoGebra Web Application V"
		        + GeoGebraConstants.VERSION_STRING);
	}

	// AG: JUST FOR TESTING!
	/*
	 * @Override public String getLoginURL(String languageCode) { return
	 * "http://tube-test.geogebra.org:8080/user/login" +
	 * "/caller/"+getURLLoginCaller()
	 * +"/expiration/"+getURLTokenExpirationMinutes()
	 * +"/clientinfo/"+getURLClientInfo() +"/?lang="+languageCode; }
	 */

	private void processToken(String token) {
		App.debug("LTOKEN send via message");
		performTokenLogin(token, false);
	}
}

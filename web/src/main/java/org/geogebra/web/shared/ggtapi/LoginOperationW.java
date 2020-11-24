package org.geogebra.web.shared.ggtapi;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.URLEncoderW;
import org.geogebra.web.shared.ggtapi.models.AuthenticationModelW;

import elemental2.dom.DomGlobal;

/**
 * The web version of the login operation. uses an own AuthenticationModel and
 * an own implementation of the API
 * 
 * @author stefan
 */
public class LoginOperationW extends LogInOperation {
	private AppW app;
	private BackendAPIFactory apiFactory;

	private class LanguageLoginCallback implements EventRenderable {
		@Override
		public void renderEvent(BaseEvent event) {
			if (isLoggedIn()) {
				app.setLanguage(getUserLanguage());
			} else {
				app.setLabels();
			}
		}
	}

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
		getView().add(new LanguageLoginCallback());
		AuthenticationModelW model = new AuthenticationModelW(appWeb);
		setModel(model);
		if (app.getVendorSettings().canSessionExpire()) {
			model.setSessionExpireTimer(app.newTimer(model,
					AuthenticationModel.SESSION_TIME));
		}

		iniNativeEvents();
		apiFactory = new BackendAPIFactory(app);
	}

	/**
	 * Handles message from login frame
	 * <ul>
	 * <li>logintoken: we got token from Tube backend
	 * <li>logincookie: user initiated login, uses cookies rather than tokens
	 * (MOW)
	 * <li>loginpassive: passive login, uses cookies
	 * </ul>
	 */
	private native void iniNativeEvents() /*-{
		var t = this;
		$wnd
				.addEventListener(
						"message",
						function(event) {
							var data;
							//later if event.origin....
							if (typeof event.data == "string") {
								try {
									data = $wnd.JSON.parse(event.data);

									if (data.action === "logintoken") {
										t.@org.geogebra.web.shared.ggtapi.LoginOperationW::processToken(Ljava/lang/String;)(data.msg);
									}
									if (data.action === "logincookie" 
										|| data.action === "loginpassive") {
										t.@org.geogebra.web.shared.ggtapi.LoginOperationW::processCookie(Z)(data.action === "loginpassive");
									}
								} catch (err) {
									@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("error occured while logging: \n" + err.message + " " + JSON.stringify(event.data));
								}
							}
						}, false);
	}-*/;

	@Override
	public BackendAPI getGeoGebraTubeAPI() {
		if (apiFactory == null) {
			apiFactory = new BackendAPIFactory(app);
		}
		return apiFactory.get();
	}

	@Override
	protected String getURLLoginCaller() {
		return "web";
	}

	@Override
	protected String getURLClientInfo() {
		URLEncoderW enc = new URLEncoderW();
		return enc.encode("GeoGebra Web Application V"
				+ GeoGebraConstants.VERSION_STRING);
	}

	@Override
	public String getLoginURL(String languageCode) {
		if (!StringUtil.empty(app.getAppletParameters().getParamLoginURL())) {
			return app.getAppletParameters().getParamLoginURL();
		}

		return super.getLoginURL(languageCode);
	}

	@ExternalAccess
	private void processToken(String token) {
		Log.debug("LTOKEN send via message");
		performTokenLogin(token, false);
	}

	private void processCookie(boolean passive) {
		Log.debug("COOKIE LOGIN");
		doPerformTokenLogin(new GeoGebraTubeUser(""), passive);
	}

	@Override
	public void showLoginDialog() {
		app.getSignInController().login();
	}

	@Override
	public void showLogoutUI() {
		if (!StringUtil.empty(app.getAppletParameters().getParamLogoutURL())) {
			DomGlobal.window.open(app.getAppletParameters().getParamLogoutURL(), "_blank",
					"menubar=off,width=450,height=350");
		}
	}

	@Override
	public void passiveLogin() {
		model.setLoginStarted();
		processCookie(true);
	}

	@Override
	protected boolean isExternalLoginAllowed() {
		return app.getLAF().isExternalLoginAllowed();
	}
}

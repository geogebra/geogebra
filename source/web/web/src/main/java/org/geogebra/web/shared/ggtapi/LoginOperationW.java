package org.geogebra.web.shared.ggtapi;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.debug.analytics.LoginAnalytics;
import org.geogebra.gwtutil.Cookies;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.AuthenticationModelW;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;
import elemental2.dom.MessageEvent;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

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
			if (isLoggedIn() && "".equals(app.getAppletParameters().getDataParamLanguage())) {
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
		getView().add(new LoginAnalytics());
		AuthenticationModelW model = new AuthenticationModelW(appWeb);
		setModel(model);

		iniNativeEvents(app);
		apiFactory = new BackendAPIFactory(app);
	}

	/**
	 * Handles message from login frame
	 * {action:"logintoken", msg:token}
	 */
	private void iniNativeEvents(AppW app) {
		app.getGlobalHandlers().addEventListener(DomGlobal.window,
						"message",
						event -> {
							MessageEvent<?> message = Js.uncheckedCast(event);
							Object data = message.data;
							// later if event.origin....
							if ("string".equals(Js.typeof(data))) {
								try {
									JsPropertyMap<Object> dataObject =
											Js.asPropertyMap(Global.JSON.parse((String) data));

									Object action = dataObject.get("action");
									if ("logintoken".equals(action)) {
										Log.debug("Login token sent via message");
										performTokenLogin((String) dataObject.get("msg"), false);
									}
								} catch (Throwable err) {
									Log.debug("error occurred while logging: \n"
											+ err.getMessage() + " " + data);
								}
							}
						});
	}

	@Override
	public BackendAPI getGeoGebraTubeAPI() {
		if (apiFactory == null) {
			apiFactory = new BackendAPIFactory(app);
		}
		return apiFactory.get();
	}

	@Override
	public MaterialRestAPI getResourcesAPI() {
		if (apiFactory == null) {
			apiFactory = new BackendAPIFactory(app);
		}
		return apiFactory.getResourcesApi();
	}

	@Override
	protected String getURLLoginCaller() {
		return "web";
	}

	@Override
	protected String getURLClientInfo() {
		return Global.encodeURIComponent("GeoGebra Web Application V"
				+ GeoGebraConstants.VERSION_STRING);
	}

	@Override
	public String getLoginURL(String languageTag) {
		if (!StringUtil.empty(app.getAppletParameters().getParamLoginURL())) {
			return app.getAppletParameters().getParamLoginURL();
		}

		return super.getLoginURL(languageTag);
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
		if (!app.isMebis()) {
			String cookie = Cookies.getCookie("SSID");
			if (cookie != null) {
				doPerformTokenLogin(new GeoGebraTubeUser(null, cookie), true);
			} else {
				stayLoggedOut();
			}
		}
	}

	@Override
	protected boolean isExternalLoginAllowed() {
		return app.getLAF() == null || app.getLAF().isExternalLoginAllowed();
	}

	public boolean loadUserFromSession() {
		return ((AuthenticationModelW) getModel()).loadUserFromSession(getGeoGebraTubeAPI());
	}
}

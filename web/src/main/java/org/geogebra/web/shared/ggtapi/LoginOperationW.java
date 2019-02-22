package org.geogebra.web.shared.ggtapi;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.Feature;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MarvlAPI;
import org.geogebra.common.move.ggtapi.operations.BackendAPI;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.views.BaseEventView;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.URLEncoderW;
import org.geogebra.web.shared.SignInButton;
import org.geogebra.web.shared.ggtapi.models.AuthenticationModelW;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The web version of the login operation. uses an own AuthenticationModel and
 * an own implementation of the API
 * 
 * @author stefan
 */
public class LoginOperationW extends LogInOperation {
	private AppW app;
	private BackendAPI api;

	private class EventViewW extends BaseEventView {
		@Override
		public void onEvent(BaseEvent event) {
			super.onEvent(event);
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
		setView(new EventViewW());
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
								try {
									data = $wnd.JSON.parse(event.data);
									if (data.action === "logintoken") {
										t.@org.geogebra.web.shared.ggtapi.LoginOperationW::processToken(Ljava/lang/String;)(data.msg);
									}
									if (data.action === "logincookie") {
										t.@org.geogebra.web.shared.ggtapi.LoginOperationW::processCookie()();
									}
								} catch (err) {
									@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("error occured while logging: \n" + err.message + " " + event.data);
								}
							}
						}, false);
	}-*/;

	@Override
	public BackendAPI getGeoGebraTubeAPI() {
		if (this.api == null) {
			if (!StringUtil
					.empty(app.getArticleElement().getParamBackendURL())) {
				this.api = new MarvlAPI(
						app.getArticleElement().getParamBackendURL(),
						new MarvlURLChecker());
			} else {
				this.api = new GeoGebraTubeAPIW(app.getClientInfo(),
						app.has(Feature.TUBE_BETA), app.getArticleElement());
			}
		} else {
			api.setClient(app.getClientInfo());
		}
		return this.api;
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
		if (!StringUtil.empty(app.getArticleElement().getParamLoginURL())) {
			return app.getArticleElement().getParamLoginURL();
		}
		return super.getLoginURL(languageCode);
	}

	private void processToken(String token) {
		Log.debug("LTOKEN send via message");
		performTokenLogin(token, false);
	}

	private void processCookie() {
		Log.debug("COOKIE LOGIN");
		doPerformTokenLogin(new GeoGebraTubeUser(""), false);
	}

	@Override
	public void showLoginDialog() {
		((SignInButton) app.getLAF().getSignInButton(app)).login();
	}

	@Override
	public void showLogoutUI() {
		if (!StringUtil.empty(app.getArticleElement().getParamLogoutURL())) {
			Window.open(app.getArticleElement().getParamLogoutURL(), "_blank",
					"menubar=off,width=450,height=350");
		}
	}

	@Override
	public void passiveLogin(final AsyncOperation<Boolean> asyncOperation) {
		if (StringUtil.empty(app.getArticleElement().getParamLoginURL())) {
			asyncOperation.callback(true);
			return;
		}
		final Frame fr = new Frame();
		fr.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				asyncOperation.callback(true);
				// fr.removeFromParent();
			}

		});
		fr.setVisible(false);
		fr.setUrl(
				app.getArticleElement().getParamLoginURL()
						+ "%3FisPassive=true&isPassive=true");
		RootPanel.get().add(fr);
	}
}
